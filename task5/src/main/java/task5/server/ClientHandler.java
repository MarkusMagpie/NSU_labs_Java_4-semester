package task5.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private Socket socket; // to establish connection between server and client
    // socket represents a connection between server(clienthandler) and client
    private BufferedReader in; // to read messages sent from client
    private BufferedWriter out; // to send messages to client
    private String userName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.userName = in.readLine(); // extracting username from first line
            synchronized (clients) {
                clients.add(this);
            }
            broadcastMessage("SERVER: " + userName + " has joined the chat."); // sending message to all clients
        } catch (IOException e) {
            closeAll();
        }
    }

    // listening for messages on separate threads
    // listening for messages is a blocking operation - program will not continue until a message is received
    @Override
    public void run() {
        String msg_from_client;
//        while (socket.isConnected()) {
//            try {
//                msg_from_client = in.readLine(); // need to be run on a separate thread
//                // so the rest of the program isn't stuck (blocking operation)
//                broadcastMessage(msg_from_client);
//            } catch (IOException e) {
//                closeAll(socket, in, out);
//                break; // client dissconects -> break out of while loop
//            }
//        }

        try {
            while ((msg_from_client = in.readLine()) != null) {
                broadcastMessage(msg_from_client);
            }
        } catch (IOException e) {
            System.out.println(userName + " has left the chat.");
        } finally {
            closeAll();
        }
    }

    // sends message to everyone in group chat
    public void broadcastMessage(String message) {
//        System.out.println("New message: " + message);
        synchronized (clients) {
            for (ClientHandler client : clients) {
                try {
                    if (!client.userName.equals(userName)) {
                        client.out.write(message);
                        client.out.newLine(); // run method uses readLine method -> need "\n"
                        client.out.flush(); // buffer will not be sent until it is full
                    }
                } catch (IOException e) {
                    closeAll();
                }
            }
        }
    }

    public void closeAll() {
        synchronized (clients) {
            clients.remove(this); // closeAll is called when we are in catch block -> user will leave
        }
        broadcastMessage("SERVER: " + userName + " has left the chat.");
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.out.println("Error closing socket: " + e.getMessage());
        }
    }
}
