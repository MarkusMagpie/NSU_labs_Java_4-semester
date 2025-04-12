package task5.java_serialize.client;

import task5.java_serialize.Message;
import task5.java_serialize.Message.Type;
import task5.java_serialize.server.ChatServer;

import javax.swing.*;
import java.awt.*;

public class ClientGUI {
    private final ChatClient client;
    private final JTextArea chatArea;
    private final JTextField messageField;

    public ClientGUI(String userName, int port, String serverHost) {
        client = new ChatClient(userName, port, serverHost, this);

        // окно чата
        JFrame frame = new JFrame("Chat - " + userName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        // область чата
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        frame.add(chatScroll, BorderLayout.CENTER);

        // поле ввода
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // сообщения отправляются по кнопке на панели
//        sendButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                sendMessage();
//            }
//        });
        sendButton.addActionListener(_ -> sendMessage());

        // отправка сообщений по Enter (не по кнопке на панели, а кнопке клавиатуры Enter)
        messageField.addActionListener(_ -> sendMessage());

        frame.setVisible(true);
        client.listenForMessages();
    }

    public void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            if (message.equalsIgnoreCase("/list")) {
                Message listMsg = new Message(Message.Type.LIST, client.getUserName(), "");
                client.sendMessage(listMsg);
                messageField.setText("");
                return;
            }
            if (message.equalsIgnoreCase("/exit")) {
                Message logoutMsg = new Message(Message.Type.LOGOUT, client.getUserName(), "left the chat");
                client.sendMessage(logoutMsg);
//                appendMessage("You have left the chat.");
                client.closeAll();
                System.exit(0);
            }
            Message msg = new Message(Type.MESSAGE, client.getUserName(), message);
            client.sendMessage(msg);
            appendMessage("You: " + message); // в свой чат пишем от лица себя
            messageField.setText("");
        }
    }

    // добавление сообщений в область чата
    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

    public static void main(String[] args) {
        String userName = JOptionPane.showInputDialog("Enter your username for the group chat:");
        if (userName != null) {
            int port = ChatServer.loadPortFromConfig();
            String serverHost = ChatClient.loadHostFromConfig();
            new ClientGUI(userName, port, serverHost);
        } else {
            System.out.println("Name can't be null...");
            System.exit(0);
        }
    }
}
