package task5.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class ChatServer {
    private ServerSocket serverSocket;
    private static int activeClients = 0;

    public ChatServer(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
            System.exit(0);
        }
    }

    public void start() {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ++activeClients;
                System.out.println("A new client has connected! Active clients: " + activeClients);
                ClientHandler handler = new ClientHandler(clientSocket);
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    public static void clientDisconnected() {
        --activeClients;
        System.out.println("A client has disconnected. Active clients: " + activeClients);
        if (activeClients == 0) {
            System.out.println("No clients connected. Server shutting down...");
            System.exit(0);
        }
    }

    public static int loadPortFromConfig() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("task5\\src\\main\\resources\\config.properties")) {
            properties.load(input);
            return Integer.parseInt(properties.getProperty("server.port", "1234"));
        } catch (IOException e) {
            System.out.println("Error loading config, using default port 1234");
            return 1234;
        }
    }

    public static void main (String[] args) throws IOException {
        int port = loadPortFromConfig(); // порт на котором сервер слушает подключения
        ChatServer server = new ChatServer(port);
        server.start();
    }
}
