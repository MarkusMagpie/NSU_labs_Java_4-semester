package task5.java_serialize.server;

import task5.java_serialize.Message;
import task5.java_serialize.Message.Type;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static task5.java_serialize.server.ChatServer.log;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private Socket socket; // to establish connection between server and client
    // socket represents a connection between server(clienthandler) and client
    private ObjectInputStream in; // to read messages sent from client
    private ObjectOutputStream out; // to send messages to client
    private String userName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // 1 полученный объект - сообщение регистрации
            Message loginMsg = (Message) in.readObject();
            if (loginMsg.getType() == Type.LOGIN) {
                this.userName = loginMsg.getSender();
                clients.add(this);
                broadcastMessage(new Message(Type.SYSTEM, "SERVER", userName + " joined the chat."));
            }
        } catch (IOException | ClassNotFoundException e) {
            closeAll();
        }
    }

    // listening for messages on separate threads
    // listening for messages is a blocking operation - program will not continue until a message is received
    @Override
    public void run() {
        try {
            Object obj;
            while ((obj = in.readObject()) != null) {
                if (obj instanceof Message) {
                    Message msg = (Message) obj;
                    if (msg.getType() == Type.LOGOUT) {
                        broadcastMessage(new Message(Type.SYSTEM, "SERVER", userName + " left the chat."));
                        break;
                    } else if (msg.getType() == Type.LIST) {
                        StringBuilder userList = new StringBuilder("Active users:\n");
                        for (ClientHandler client : clients) {
                            userList.append(client.userName).append("\n");
                        }
                        Message listResponse = new Message(Type.LIST, "SERVER", userList.toString());
                        // отправляем ответ только запрашивающему клиенту
                        out.writeObject(listResponse);
                        out.flush();
                    } else if (msg.getType() == Type.MESSAGE) {
                        broadcastMessage(msg);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving messages from client: " + e.getMessage());
        } finally {
            closeAll();
            ChatServer.clientDisconnected(); // уведомил сервер о выходе клиента
        }
    }

    // sends message to everyone in group chat
    public void broadcastMessage(Message msg) {
        for (ClientHandler client : clients) {
            try {
                if (!client.userName.equals(this.userName)) {
                    client.out.writeObject(msg);
                    client.out.flush();
                }
            } catch (IOException e) {
                closeAll();
            }
        }
    }

    public void closeAll() {
        clients.remove(this); // closeAll is called when we are in catch block -> user will leave
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            log("Error closing socket: " + e.getMessage());
        }
    }
}
