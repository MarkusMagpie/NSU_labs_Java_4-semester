package task5.client;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private String userName;
    private ClientGUI gui;

    public ChatClient(String userName, int port, String serverHost, ClientGUI gui) {
        try {
            this.socket = new Socket(serverHost, port); // создаёт TCP-соединение с сервером,
            // запущенным на этом же компьютере (localhost), и прослушивающим указанный порт.
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = userName;
            out.write(userName); // sending username to server
            out.newLine();
            out.flush();

            this.gui = gui;
        } catch (IOException e) {
            System.out.println("Unable to connect to server: " + e.getMessage());
            System.exit(0);
        }
    }

    public void sendMessage(String msg) {
//        try {
//            Scanner scanner = new Scanner(System.in);
//            while (socket.isConnected()) {
//                String msg_to_send = scanner.nextLine();
//                if (msg_to_send.equalsIgnoreCase("/exit")) {
//                    closeAll();
//                    break;
//                }
//                out.write(userName + ": " + msg_to_send);
//                out.newLine();
//                out.flush();
//            }
//        } catch (IOException e) {
//            closeAll();
//        }
        try {
            if (msg.equalsIgnoreCase("/exit")) {
                closeAll();
                return;
            }
            out.write(userName + ": " + msg);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            closeAll();
        }
    }

    public void listenForMessages() {
        new Thread(() -> {
            String msg_from_chat;

            try {
                while ((msg_from_chat = in.readLine()) != null) {
                    gui.appendMessage(msg_from_chat);
                    System.out.println(msg_from_chat);
                }
            } catch (IOException e) {
                System.out.println("Error receiving messages: " + e.getMessage());
            } finally {
                closeAll();
            }
        }).start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String msg_from_chat;
//
//                while (socket.isConnected()) {
//                    try {
//                        msg_from_chat = in.readLine();
//                        System.out.println(msg_from_chat);
//                    } catch (IOException e) {
//                        closeAll();
//                    }
//                }
//            }
//        }).start();
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

//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter your username for the group chat: ");
//        String userName = scanner.nextLine();
//
//        int port = loadPortFromConfig();
//        String serverHost = loadHostFromConfig();
//        ChatClient client = new ChatClient(userName, port, serverHost);
//        client.listenForMessages();
//        client.sendMessage();
//    }
}

