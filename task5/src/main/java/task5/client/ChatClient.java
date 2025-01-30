package task5.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private String userName;

    public ChatClient(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = userName;
        } catch (IOException e) {
            closeAll(socket, out, in);
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
                out.write(userName + ": " + msg_to_send);
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            closeAll(socket, out, in);
        }
    }

    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg_from_chat;

                while (socket.isConnected()) {
                    try {
                        msg_from_chat = in.readLine();
                        System.out.println(msg_from_chat);
                    } catch (IOException e) {
                        System.out.println("Connection with server lost: " + e.getMessage());
                    }
                }
            }
        }).start();
    }

    public void closeAll(Socket socket, BufferedWriter out, BufferedReader in) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException ignored) {
            System.out.println("Error closing socket: " + ignored.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String userName = scanner.nextLine();

        Socket socket = new Socket("localhost", 1234);

        ChatClient client = new ChatClient(socket, userName);
        client.listenForMessages(); // both methods are blocking methods cuz both have infinite loops
        client.sendMessage();
    }
}

