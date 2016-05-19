package codegenerator;


import codegenerator.web.HttpRequest;
import codegenerator.web.HttpServer;
import com.google.gson.Gson;

import java.io.File;
import java.util.*;

/**
 * Класс управления многопоточного майнинга.
 * <p/>
 * Для того что бы генерировать код на нескольких устройствах, задача генерации была поделена на равные блоки.
 * И для того что бы новое подключенное устройство не майнило уже смайненые блоки, и был создан данный класс.
 */
public class Syncronizator implements Runnable {

    /**
     * Глобальные переменные обернуты в класс для последующей передачи их дружественным устройствам.
     */
    public static GlobalVars data = new GlobalVars();

    public static Gson json = new Gson();

    public Syncronizator(Integer activeThreadCount, Integer blockSize, Long lastBlockID, Long endBlockID, File propertyDir) {
        data.activeThreadCount = activeThreadCount;
        data.blockSize = blockSize;
        data.lastBlockID = lastBlockID;
        data.endBlockID = endBlockID;
        data.propertyDir = propertyDir;
    }

    /**
     * Список выполняемых и выполненых потоков майнинга
     */
    static Map<Long, Thread> threads = Collections.synchronizedMap(new HashMap<Long, Thread>());


    /**
     * История последних 500 лог-сообщений
     */
    public static ArrayList<String> logHistory = new ArrayList<String>();

    public static String getLogHistory() {
        String result = "";
        for (String logLine : logHistory)
            result += logLine;
        return result;
    }

    public static synchronized void log(String str) {
        // В начале идут самые последние
        System.out.println(str);
        logHistory.add(0, str + "<br/>\n");
        // Ограничение длинны логов до 500 последних записей
        if (logHistory.size() > 500)
            logHistory.remove(logHistory.size() - 1);
    }


    @Override
    public void run() {

        /**
         * Загружаем файл с глобальными переменными сохраненными в прошлую сессию
         */
        data.loadFromFile();

        /**
         * Стартуем http сервер
         */
        new Thread(new HttpServer()).start();

        /**
         * Стартуем основной цикл майнинга. Он работает со списком потоков майнинга.
         * Один поток обрабатывает свою часть задания - блоки.
         * Потоки могут быть в нескольких состояниях:
         * 1) Мертвый - после загрузки оказалось что поток был не завершен а приложение было закрыто
         * 2) Работающий - поток который майнится в данный момент времени
         * 3) Удаленный - поток который выполняется на другом сервере
         * 4) Оконченный - поток завершился успешно и ждет удаления
         * После удаления ненужных блоков запускаются потоки майнинга и весь цикл повторяется сначала с задержкой в 2сек.
         */
        log("start function generate from ID " + data.lastBlockID);
        while (true) {

            //Чистим список блоков от метрвых блоков.
            for (Iterator<Map.Entry<Long, Block>> it = data.blocks.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Long, Block> entry = it.next();
                Block block = entry.getValue();
                Thread thread = threads.get(block.threadID);
                // если чужой блок не был закончен в отведенное ему время считаем что устройство было выключено.
                if ((block.ID > data.lastBlockID && !block.mac.equals(data.mac) &&
                        (block.endTime <= System.currentTimeMillis() && block.threadEnd == false))
                        ||
                        //или если свой блок не был закончен и не майнится сейчас то считается мертвым.
                        (block.threadID > data.lastBlockID && block.mac.equals(data.mac)
                                && block.threadEnd == false && thread == null)) {
                    //удаляем мертвый блок из списка блоков.
                    it.remove();
                }
            }

            //Опрашиваем дружественные устройства о уже намайненых блоках, что бы не майнить то что уже было смайнено.
            for (int j = 0; j < data.hosts.size(); j++) {
                String remoteAddress = data.hosts.get(j);
                //если свой ip то не отправляем запрос.
                if (remoteAddress.equals(data.ip + ":" + data.httpPort))
                    continue;
                // Отправление запроса на получение чужих глобальных данных
                String response = HttpRequest.getHTML("http://" + remoteAddress);
                if ("".equals(response)) //если устройство выключено или ошибка запроса.
                    continue;
                //производим десериализацию.
                GlobalVars remoteGlobalVars = json.fromJson(response, GlobalVars.class);
                if (remoteGlobalVars == null)
                    continue;

                //Останавливаем потоки и удаляем у себя блоки которые уже смайнило дружественное устройство или ещё майнит.
                for (Iterator<Map.Entry<Long, Block>> it = data.blocks.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<Long, Block> entry = it.next();
                    Block localBlock = entry.getValue();
                    Block remoteBlock = remoteGlobalVars.blocks.get(localBlock.ID);
                    // если мы майним блок который уже смайнил или майнит дружественное устройство то перестаем майнить.
                    if ((localBlock.ID > data.lastBlockID && localBlock.ID > remoteGlobalVars.lastBlockID &&
                            (remoteBlock != null &&
                                    ((remoteBlock.threadEnd == true) ||
                                            (remoteBlock.threadEnd == false && !remoteBlock.mac.equals(data.mac)))))
                            ||
                            //это пропускаем.
                            (localBlock.ID > data.lastBlockID && localBlock.ID <= remoteGlobalVars.lastBlockID &&
                                    (remoteBlock == null) ||
                                    (remoteBlock != null && !remoteBlock.mac.equals(data.mac)))) {
                        //останавливаем свой поток майнинга.
                        Thread thread = threads.get(localBlock.threadID);
                        if (localBlock.mac.equals(data.mac) && thread != null && thread.isAlive())
                            thread.interrupt();
                        //удаляем лишний блок из списка блоков.
                        it.remove();
                    }
                }

                //Добавляем в свой список блоков все блоки которые майнятся удаленно.
                for (Long blockID : remoteGlobalVars.blocks.keySet()) {
                    Block remoteBlock = remoteGlobalVars.blocks.get(blockID);
                    if ((remoteBlock.ID > data.lastBlockID) && (!remoteBlock.mac.equals(data.mac)))
                        data.blocks.put(blockID, remoteBlock);
                }

                //Устанавливаем последний смайненый блок на чужой, что бы брать точно не смайненые блоки.
                if (data.lastBlockID < remoteGlobalVars.lastBlockID) {
                    log("merge host " + remoteAddress + " arrayID " + data.lastBlockID + "->" + remoteGlobalVars.lastBlockID);
                    data.lastBlockID = remoteGlobalVars.lastBlockID;
                }
            }


            //Удаляем потоки которые закончили майнинг. Пусть покоятся с миром.
            for (Long threadID : threads.keySet()) {
                Thread thread = threads.get(threadID);
                if (!thread.isAlive())
                    threads.remove(thread);
            }

            //Смещаем идентификатор последнего смайненого блока.
            while (true) {
                Block block = data.blocks.get(data.lastBlockID + 1);
                //Если блок закончил зарботу
                if ((block != null) && (block.threadEnd == true)) {
                    //Если не найдено в блоке ничего что нам нужно то удаляем блок, что бы не плодить пустые блоки.
                    if (block.goodFunctionID.size() == 0)
                        data.blocks.remove(block);
                    data.lastBlockID++;
                    //Выводим в консоль каждые 10 смайненых блоков номер последнего блока.
                    if (data.lastBlockID % 10 == 0)
                        log("lastBlockID " + data.lastBlockID);
                } else
                    break;
            }


            //Запускаем новые потоки майнинга.
            if (data.lastBlockID <= data.endBlockID)
                for (int j = threads.size() - 1; j < data.activeThreadCount - 1; j++) {

                    //Вычисляем идентификатор несмайненого блока.
                    Long nextBlockID = data.lastBlockID + 1;
                    while (true) {
                        Block block = data.blocks.get(nextBlockID);
                        if (block == null)
                            break;
                        //Если где то майнится то проверяем следующий идентификатор.
                        nextBlockID++;
                    }

                    //Запускаем генератор кода для блока в новом потоке с минимальным приоритетом.
                    Thread newThread = new Thread(new CodeGenerator(data.blockSize, nextBlockID));
                    newThread.setPriority(Thread.MIN_PRIORITY);
                    threads.put(newThread.getId(), newThread);

                    //Регистрируем новый блок в списке блоков.
                    Block block = new Block();
                    block.ID = nextBlockID;
                    block.endTime = System.currentTimeMillis() + 60000;
                    block.mac = data.mac;
                    block.threadID = newThread.getId();
                    data.blocks.put(nextBlockID, block);

                    //И как говорил Гагарин: "Поехали!"
                    newThread.start();
                }


            data.saveToFile();

            // Выходим когда все блоки были смайнены
            if (threads.size() == 0)
                return;

            //Делаем задержку между проверками синхронизатора потоков.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log("Sync thread is stop" + e.getMessage());
            }
        }
    }


}
