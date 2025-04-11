package task5.java_serialize.client;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

import task5.java_serialize.Message;
import task5.java_serialize.Message.Type;

public class ChatClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String userName;
    private ClientGUI gui;

    public ChatClient(String userName, int port, String serverHost, ClientGUI gui) {
        try {
            socket = new Socket(serverHost, port); // sockets provide a connection mechanism between 2 computers using TCP
            // socket connection - 2 machines have information about each other's network location (IP and port)
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            this.userName = userName;
            this.gui = gui;

            // отправляем сообщение регистрации серверу
            // сервер разошлет его всем клиентам кроме самого отправителя
            Message loginMsg = new Message(Type.LOGIN, userName, "joined the chat");
            sendMessage(loginMsg);
        } catch (IOException e) {
            System.out.println("Unable to connect to server: " + e.getMessage());
            System.exit(0);
        }
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg); // записывает в поток отдельный объект
            out.flush(); // очищает буфер и сбрасывает его содержимое в выходной поток
        } catch (IOException e) {
            closeAll();
        }
    }

    public void listenForMessages() {
        new Thread(() -> {
            try {
                Object obj;
                while ((obj = in.readObject()) != null) {
                    if (obj instanceof Message) {
                        Message msg = (Message) obj;
                        gui.appendMessage(msg.toString());
                        System.out.println(msg);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error receiving messages: " + e.getMessage());
            } finally {
                closeAll();
            }
        }).start();
    }

    public void closeAll() {
        try {
            if (socket != null) socket.close();
            if (out != null) out.close();
            if (in != null) in.close();
        } catch (IOException e) {
            System.out.println("Error closing socket: " + e.getMessage());
        }
    }

    public static String loadHostFromConfig() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("task5/src/main/resources/config.properties")) {
            properties.load(input);
            return properties.getProperty("server.host", "localhost");
        } catch (IOException e) {
            System.out.println("Error loading config, using default host: localhost");
            return "localhost";
        }
    }

    public String getUserName() {
        return userName;
    }
}

