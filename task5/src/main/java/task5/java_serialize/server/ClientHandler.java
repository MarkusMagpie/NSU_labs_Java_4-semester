package task5.java_serialize.server;

import task5.java_serialize.Message;
import task5.java_serialize.Message.Type;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

                //  новому клиенту даю историю сообщений
                for (Message m : ChatServer.messageHistory) {
                    out.writeObject(m);
                }
                out.flush();

                Message msg = new Message(Type.SYSTEM, "SERVER", userName + " joined the chat.");
                ChatServer.addMessageToHistory(msg); // добавляем сообщение о регистрации в историю
                broadcastMessage(msg);
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
                        Message logoutMsg = new Message(Type.SYSTEM, "SERVER", userName + " left the chat.");
                        ChatServer.addMessageToHistory(logoutMsg);
                        broadcastMessage(logoutMsg);
                        break;
                    } else if (msg.getType() == Type.LIST) {
                        List<String> userNames = new ArrayList<>();
                        for (ClientHandler client : clients) {
                            userNames.add(client.userName);
                        }

                        String userList = "Active users:\n" + String.join("\n", userNames);
                        Message listResponse = new Message(Type.LIST, "SERVER", userList);
                        // отправляем ответ только запрашивающему клиенту!!!
                        out.writeObject(listResponse);
                        out.flush();
                    } else if (msg.getType() == Type.MESSAGE) {
                        ChatServer.addMessageToHistory(msg);
                        broadcastMessage(msg);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving messages from client: " + e.getMessage());
        } finally {
            closeAll();
        }
    }

    // sends message to everyone in group chat
    public void broadcastMessage(Message msg) {
        for (ClientHandler client : clients) {
            try {
                if (msg.getType() == Type.MESSAGE && client.userName.equals(this.userName)) {
                    continue;
                }
                client.out.writeObject(msg);
                client.out.flush();
            } catch (IOException e) {
                closeAll();
            }
        }
    }

    public void closeAll() {
        clients.remove(this); // closeAll is called when we are in catch block -> user will leave
        ChatServer.clientDisconnected(); // уведомил сервер о выходе клиента
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            log("Error closing socket: " + e.getMessage());
        }
    }
}
