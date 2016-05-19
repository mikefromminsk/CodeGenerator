package codegenerator.web;

import codegenerator.GlobalVars;
import codegenerator.Syncronizator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Класс открывает 8080 порт и создает две странички
 * (/) все смайненые данные
 * (/log) логи
 */
public class HttpServer implements Runnable {

    public void run() {
        int port = GlobalVars.httpPort;
        while (true) {
            try {
                Syncronizator.log("try open " + port + " port");
                ServerSocket ss = new ServerSocket(port);
                Syncronizator.log("HttpServer open on " + port + " port");
                Syncronizator.data.httpPort = port;
                while (true) {
                    Socket s = ss.accept();
                    new Thread(new Response(s)).start();
                }
            } catch (Throwable e) {
                // порт занят и ищем другой не занятый
                Syncronizator.log("port " + port + " closed");
                port++;
            }
        }
    }

    static class Response implements Runnable {

        private Socket s;
        private InputStream is;
        private OutputStream os;

        private Response(Socket s) throws Throwable {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
        }

        public void run() {
            try {
                // Читаем хидеры и первую строчку с адресом сохраняем
                String firstLine = null;
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while (true) {
                    String readLine = null;
                    try {
                        readLine = br.readLine();
                        if (firstLine == null)
                            firstLine = readLine;
                    } catch (IOException e) {
                        Syncronizator.log("read header error " + e.getMessage());
                    }
                    if (readLine == null || readLine.trim().length() == 0)
                        break;
                }
                // На сервере всего две странички это страничка логов(/log) и смайнеными данными(/)
                String resultData;
                if (firstLine.indexOf("log") != -1)
                    resultData = Syncronizator.getLogHistory();
                else
                    resultData = Syncronizator.json.toJson(Syncronizator.data);

                // Отправляем данные
                String response = "HTTP/1.1 200 OK\r\n" +
                        "Server: YarServer/2009-09-09\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + resultData.length() + "\r\n" +
                        "Connection: close\r\n\r\n";
                String result = response + resultData;
                try {
                    os.write(result.getBytes());
                    os.flush();
                } catch (IOException e) {
                    Syncronizator.log("write result error" + e.getMessage());
                }
            } finally {
                try {
                    s.close();
                } catch (IOException e) {
                    Syncronizator.log("socket close error" + e.getMessage());
                }
            }
        }


    }
}