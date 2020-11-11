/**
 * Created by Дмитрий on 12.08.2020.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

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

    public JTextField getUserName() {
        return UserName;
    }

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
        JLabel clientsSum = new JLabel("Клиентов в чате = 0");
        UserName = new JTextField("Имя  ");
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

        // Оптправляем сообщения и принимаем
        button.addActionListener((ActionEvent e) -> {

            String message = UserName.getText() + ":  " + inputMessage.getText();
            try {
                writer.write(message);
                writer.newLine();
                writer.flush();
                System.out.println("Request: " + message);
            } catch (IOException e2) {
                e2.printStackTrace();
            }

                new Thread(() -> {
                    while (true) {
                        String inMes = null;
                        try {
                            inMes = reader.readLine();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        if (inMes != null) {
                            int index = inMes.indexOf("Клиентов в чате =");
                            if (index != -1)
                                clientsSum.setText(inMes);
                            else
                                ChatPlace.append(inMes + "\n");
                        }
                    }
                }).start();

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    try {
                        writer.append("Клиент " + UserName.getText() + " вышел из чата!");
                        writer.flush();
                        writer.close();
                        reader.close();
                    } catch (IOException c) {

                    }
                }
            });

            inputMessage.setText("");
        });
    }
}