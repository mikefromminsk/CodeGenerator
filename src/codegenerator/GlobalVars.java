package codegenerator;

import com.google.gson.Gson;

import java.io.*;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/*
*  Класс содержит глобальные переменные текущего хоста для передачи другим хостам для создания сети майнинга
* */
public class GlobalVars implements Serializable {
    /**
     * Папка с сохраненными глобальными данными прошлой сессии.
     */
    public static File propertyDir;
    /**
     * Данные о своем хосте
     */
    static String ip = getHostAddress();
    /**
     * Порт Http сервера
     * может измениться если данный порт занят. Будут перебераться все последующие порты 8081..65535
     */
    public static Integer httpPort = 8080;
    /**
     * Мак адрес нужен для идентификации блоков. А именно какое устройство майнит блок.
     */
    static String mac = getMac();
    /**
     * Количество потоков для майнинга
     * установлено статически так как у меня есть только два устройства и у каждого по 4 ядра
     * можно изменить на свое значение ядер.
     * В данный момент инициализируется в конструкторе класса Sync
     * TODO дописать автоматическое определение количества ядер
     */
    static Integer activeThreadCount = 4;
    /**
     * Порядковый идентификатор последней намайненой фунции
     */
    Long lastBlockID = 1L;
    /**
     * Намайненые и блоки которые ещё майнятся блоки
     */
    public Map<Long, Block> blocks = Collections.synchronizedMap(new HashMap<Long, Block>());
    /**
     * известные хосты для майнинга блоков
     */
    public ArrayList<String> hosts = new ArrayList<String>();
    /**
     * Количество генерируемых функций в блоке
     */
    public Integer blockSize;
    /**
     * Последний блок майнинга. Нужен для остановки вечного майнинга.
     */
    public Long endBlockID;


    public static InetAddress getCurrentIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) networkInterfaces
                        .nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while (nias.hasMoreElements()) {
                    InetAddress ia = (InetAddress) nias.nextElement();
                    if (!ia.isLinkLocalAddress()
                            && !ia.isLoopbackAddress()
                            && ia instanceof Inet4Address) {
                        return ia;
                    }
                }
            }
        } catch (SocketException e) {
            //LOG.error("unable to get current IP " + e.getMessage(), e);
        }
        return null;
    }

    public static String getMac() {
        byte[] mac = new byte[0];
        try {
            mac = NetworkInterface.getByInetAddress(getCurrentIp()).getHardwareAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++)
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        return sb.toString();
    }

    private static String getHostAddress() {
        return getCurrentIp().getHostAddress();
    }

    void loadFromFile(){
        try {
            String strHostData = new Scanner(new File(propertyDir, GlobalVars.class.getSimpleName())).useDelimiter("\\Z").next();
            if (!strHostData.equals("")) {
                GlobalVars globalVars = Syncronizator.json.fromJson(strHostData, GlobalVars.class);
                this.blocks = globalVars.blocks;
                this.lastBlockID = globalVars.lastBlockID;
                this.hosts = globalVars.hosts;
            }

        } catch (Exception e) {
            Syncronizator.log("settingFile not found");
        }
    }

    public void saveToFile() {
        try {
            if (propertyDir != null) {
                FileWriter fw = new FileWriter(new File(propertyDir, GlobalVars.class.getSimpleName()));
                fw.write(Syncronizator.json.toJson(this));
                fw.close();
            }
        } catch (Exception e) {
            Syncronizator.log("global vars file not save");
        }
    }
}