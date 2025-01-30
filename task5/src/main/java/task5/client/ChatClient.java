package task5.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static task5.server.ChatServer.loadPortFromConfig;

public class ChatClient {
    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private String userName;

    public ChatClient(int port, String userName) {
        try {
            this.socket = new Socket("localhost", port); // создаёт TCP-соединение с сервером,
            // запущенным на этом же компьютере (localhost), и прослушивающим указанный порт.
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = userName;
        } catch (IOException e) {
            System.out.println("Unable to connect to server: " + e.getMessage());
            System.exit(0);
        }
    }

    public void sendMessage() {
        try {
            out.write(userName);
            out.newLine();
            out.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String msg_to_send = scanner.nextLine();
                if (msg_to_send.equalsIgnoreCase("/exit")) {
                    closeAll();
                    break;
                }
                out.write(userName + ": " + msg_to_send);
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            closeAll();
        }
    }

    public void listenForMessages() {
        new Thread(() -> {
            String msg_from_chat;

            try {
                while ((msg_from_chat = in.readLine()) != null) {
                    System.out.println(msg_from_chat);
                }
            } catch (IOException e) {
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
        } catch (IOException ignored) {
            System.out.println("Error closing socket: " + ignored.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String userName = scanner.nextLine();

        int port = loadPortFromConfig();
        ChatClient client = new ChatClient(port, userName);
        client.listenForMessages();
        client.sendMessage();
    }
}

