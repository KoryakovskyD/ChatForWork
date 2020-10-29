/**
 * Created by Дмитрий on 12.08.2020.
 */
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends JFrame{

    private final int LOCATION = 200;
    private final int WIDTH = 850;
    private final int HEIGHT = 450;
    private final String ServerName = "localhost";
    private final int port = 4444;
    private JTextField inputMessage;
    private JTextField UserName;
    private JTextArea ChatPlace;
    private BufferedReader reader;
    private BufferedWriter writer;

    public Client() {

        setTitle("Добро пожаловать в чат!");
        setBounds(LOCATION, LOCATION, WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChatPlace = new JTextArea();
        ChatPlace.setLineWrap(true);
        ChatPlace.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(ChatPlace);
        add(scrollPane, BorderLayout.CENTER);
        inputMessage = new JTextField("");
        JLabel clientsSum = new JLabel("Людей в чате: ");
        UserName = new JTextField("Имя  .....");
        JButton button = new JButton("  Отправить  ");

        add(clientsSum, BorderLayout.NORTH);
        JPanel panel = new JPanel(new BorderLayout());
        add(panel, BorderLayout.SOUTH);
        panel.add(UserName, BorderLayout.WEST);
        panel.add(inputMessage, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);
        setVisible(true);


        try {
            Socket socket = new Socket(ServerName, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        button.addActionListener(e -> {

            String message = UserName.getText() + ":  " + inputMessage.getText();
            try {
                writer.write(message);
                writer.newLine();
                writer.flush();
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            try {
               new Thread(new Runnable() {
                   public void run() {
                       // бесконечный цикл
                       while (true) {
                           // если есть входящее сообщение
                           if (reader != null) {
                               System.out.println("-------------");
                               // считываем его
                               String inMes = null;
                               try {
                                   inMes = reader.readLine();
                               } catch (IOException e1) {
                                   e1.printStackTrace();
                               }
                                   // выводим сообщение
                                   ChatPlace.append(inMes);
                                   // добавляем строку перехода
                                   ChatPlace.append("\n");
                           }
                       }
                   }
               }).start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }
}