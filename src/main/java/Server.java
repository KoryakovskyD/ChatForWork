/**
 * Created by Дмитрий on 12.08.2020.
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private static int port = 4444;
    private static ArrayList<NewClient> clients = new ArrayList<>();


    public Server() {
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Сервер запущен!");

            Socket socket;

            while (true) {
                socket = server.accept();
                NewClient newClient = new NewClient(socket);
                clients.add(newClient);
                new Thread(newClient).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        // отправляем сообщение всем клиентам
        public static void sendMessageToAllClients(String msg) {
            for (NewClient o : clients) {
                o.sendMsg(msg);
            }

        }

        // удаляем клиента из коллекции при выходе из чата
        public static void removeClient(NewClient client) {
            clients.remove(client);
        }

}