/**
 * Created by Дмитрий on 29.10.2020.
 */
import java.io.*;
import java.net.Socket;



// реализуем интерфейс Runnable, который позволяет работать с потоками
public class NewClient implements Runnable {

    private BufferedReader reader;
    private BufferedWriter writer;
    // клиентский сокет
    private Socket clientSocket = null;
    // количество клиента в чате, статичное поле
    private static int clients_count = 0;

    // конструктор, который принимает клиентский сокет и сервер
    public NewClient(Socket socket) {
        try {
            clients_count++;
            this.clientSocket = socket;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // сервер отправляет сообщение
                Server.sendMessageToAllClients("Новый участник вошёл в чат!");
                Server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
                break;
            }


            while (true) {
                // Если от клиента пришло сообщение
                if (reader != null) {
                    String clientMessage = reader.readLine();
                    if (clientMessage.contains("вышел из чата!")) {
                        break;
                    }
                    // отправляем данное сообщение всем клиентам
                    Server.sendMessageToAllClients(clientMessage);
                }
                // останавливаем выполнение потока на 100 мс
                Thread.sleep(100);
            }

        }
        catch (InterruptedException |  IOException ex) {
            ex.printStackTrace();
        }
        finally {
            this.close();
        }
    }

    // отправляем сообщение

    public void sendMsg(String msg) {
        try {
            writer.append(msg);
            writer.newLine();
            writer.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // клиент выходит из чата
    public void close() {
        // удаляем клиента из списка
        Server.removeClient(this);
        clients_count--;
        Server.sendMessageToAllClients("Клиент покинул чат!");
        Server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
    }
}
