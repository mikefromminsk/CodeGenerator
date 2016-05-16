package codegenerator;

import codegenerator.utils.Base64;

import java.io.*;
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
    Integer lastBlockID = 0;
    /**
     * Намайненые и блоки которые ещё майнятся блоки
     */
    public Map<Integer, Block> blocks = Collections.synchronizedMap(new HashMap<Integer, Block>());
    /**
     * известные хосты для майнинга блоков
     */
    public ArrayList<String> hosts = new ArrayList<String>();


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


    public static synchronized GlobalVars deserializeGlobalVars(String s) {
        byte[] data = codegenerator.utils.Base64.decode(s);
        ObjectInputStream ois = null;
        Object o = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(data));
            o = ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (GlobalVars)o;
    }

    void loadFromFile(){
        try {
            String strHostData = new Scanner(new File(propertyDir, GlobalVars.class.getSimpleName())).useDelimiter("\\Z").next();
            if (!strHostData.equals("")) {
                GlobalVars globalVars = deserializeGlobalVars(strHostData);
                this.blocks = globalVars.blocks;
                this.lastBlockID = globalVars.lastBlockID;
                this.hosts = globalVars.hosts;
            }

        } catch (FileNotFoundException e) {
            //Простая заплаточка со списком моих хостов. Сделана что бы не использовать централизованный сервер.
            // TODO Сделать сканер сети
            this.hosts.add("192.168.1.10:8080");
            this.hosts.add("192.168.1.10:8081");
            this.hosts.add("192.168.1.8:8080");
        }
    }


    public static synchronized String serializeGlobalVars(GlobalVars o) {
        ByteArrayOutputStream baos = null;

        try {
            baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(Base64.encode(baos.toByteArray()));
    }

    public void saveToFile() {
        try {
            FileWriter fw = new FileWriter(new File(propertyDir, GlobalVars.class.getSimpleName()));
            fw.write(serializeGlobalVars(this));
            fw.close();
        } catch (IOException e) {
            Sync.log("global vars file not save");
        }
    }

    public static String getAllMiningData() {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(Sync.data);
            oos.close();
        } catch (IOException e) {
            Sync.log("serializable error" + e.getMessage());
        }
        return new String(Base64.encode(baos.toByteArray()));
    }
}