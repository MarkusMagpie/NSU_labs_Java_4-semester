# Отчет по 5 заданию "Сетевое программирование, сериализация, XML."

Отчет разделен на описание двух версий клиента/сервера:
1. сериализация/десериализация Java-объектов для посылки/приема сообщений
2. XML сообщения

## 1 Сериализация/десериализация Java-объектов для посылки/приема сообщений
### 1.1 Структура проекта:
Сервер:  
 - `ChatServer` - запускает сервер, управляет подключениями, логгирует события, происходящие на его стороне  
 - `ClientHandler` - обработчик взаимодействия сервера с каждым клиентом. 
Объект этого класса создается в `ChatServer` сразу после установки соединения с 
новым клиентом.

Клиент:  
 - `ChatClient` - устанавливает соединение с сервером, отправляет/принимает сообщения.
 - `ClientGUI` - графический интерфейс юзера.

Общий класс:  
 - `Message` — сериализуемый объект для передачи данных между клиентом и сервером.



### 1.2 Логика и теория для проекта:
#### 1.2.1 РАБОТА С СОКЕТАМИ  
В Java для реализации TCP-соединения между клиентом и сервером используются классы `Socket` и `ServerSocket`:  

- `Socket` используется для создания клиентского сокета, который подключается к серверу.
Он представляет собой конечную точку соединения TCP между клиентом и сервером, обеспечивает средства для отправки и 
получения данных через сеть.
  
- `ServerSocket` предназначен для создания серверного сокета, который принимает входящие соединения от клиентов.
Он ожидает подключения на определённом порту и при успешном подключении создаёт новый сокет для общения с клиентом.

Побольше можно читать [здесь](https://javatutor.net/books/tiej/socket).



#### 1.2.2 СЕРИАЛИЗАЦИЯ И ДЕСИАРЕЛИЗАЦИЯ  
Сериализация - процесс записи состояния объекта в поток.  
Десериализация - процесс извлечения или восстановления состояния объекта из потока. 
Класс `Message` сериализуется и передается между клиентом и сервером. 

Как оно выглядит в коде:  
Клиент отправляет объект `Message` через `ObjectOutputStream`:
```java
out.writeObject(msg); // записывает в поток отдельный объект
out.flush(); // записывает в поток отдельный объект
```
Сервер в `ClientHandler` читает объект через `ObjectInputStream`:
```java
Message loginMsg = (Message) in.readObject();
```
и в зависимости от типа сообщения его обрабатывает.



#### 1.2.3 ПРИМЕР ВЗАИМОДЕЙСТВИЯ МЕЖДУ КЛИЕНТОМ И СЕРВЕРОМ - ПОДКЛЮЧЕНИЕ КЛИЕНТА 
1. Подключение клиента:
запускаю функцию `main` в классе `ClientGUI` которая запустит одноименный конструктор
в котором создается объект 
```java
client = new ChatClient(userName, port, serverHost, this);
``` 
в свою очередь конструктор `ChatClient` отсылает сообщение:
```java
Message loginMsg = new Message(Type.LOGIN, userName, "joined the chat");
sendMessage(loginMsg);
```
2. Сервер в конструкторе `ClientHandler` добавляет клиента в список и рассылает уведомление:
```java
broadcastMessage(new Message(Type.SYSTEM, "SERVER", userName + " joined the chat."));
```


### 1.3 `Message` class
```java
public class Message implements Serializable {
    public enum Type {
        LOGIN, // регистрация юзера
        LOGOUT,
        MESSAGE, // обычное сообщение
        LIST, // запрос списка пользователей
        SYSTEM // системные сообщения (подкл/откл)
    }

    private final Type type;
    private final String sender;
    private final String content;

    public Message(Type type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    public Type getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        if (type == Type.MESSAGE)
            return sender + ": " + content;
        else
            return content;
    }
}
```
`Message` реализует интерфейс `Serializable`, что позволяет сериализовать 
объекты в байтовый поток для передачи по сети. 
Каждое сообщение в системе (login, logout, message и тд) 
инкапсулируется в объект именно этого класса.

> Сериализовать можно только те объекты, которые реализуют интерфейс `Serializable`. 
> Этот интерфейс не определяет никаких методов, просто он служит указателем системе, 
> что объект, реализующий его, может быть сериализован.

> Для сериализации объектов в поток используется класс `ObjectOutputStream`. 
> Он записывает данные в поток.

> Класс `ObjectInputStream` отвечает за обратный процесс - чтение ранее 
> сериализованных данных из потока.

Читать про сериализацию [здесь](https://metanit.com/java/tutorial/6.10.php).  



### 1.4 `server` package
#### 1.4.1 `ChatServer` class
```java
public class ChatServer {
    private static final Logger logger = LogManager.getLogger(ChatServer.class.getName());
    private static boolean loggingEnabled;

    private ServerSocket serverSocket;
    private static int activeClients = 0;

    public ChatServer(int port, boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
        try {
            this.serverSocket = new ServerSocket(port);
            // очистка файла логгирования
            new FileWriter("task5\\src\\main\\resources\\serverlog.txt", false).close();
            log("Server started on port: " + port);
        } catch (IOException e) {
            log("Error starting server: " + e.getMessage());
            System.exit(0);
        }
    }

    public void start() {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ++activeClients;
                log("A new client has connected! Active clients: " + activeClients);
                ClientHandler handler = new ClientHandler(clientSocket);
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException e) {
            log("Error starting server: " + e.getMessage());
        }
    }

    public static void clientDisconnected() {
        --activeClients;
        log("A client has disconnected. Active clients: " + activeClients);
        if (activeClients == 0) {
            log("No clients connected. Server shutting down...");
            System.exit(0);
        }
    }

    public static int loadPortFromConfig() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("task5\\src\\main\\resources\\config.properties")) {
            properties.load(input);
            return Integer.parseInt(properties.getProperty("server.port", "1234"));
        } catch (IOException e) {
//            System.out.println("Error loading config, using default port 1234");
            log("Error loading config, using default port 1234");
            return 1234;
        }
    }

    public static boolean loadLoggingFromConfig() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("task5/src/main/resources/config.properties")) {
            properties.load(input);
            return Boolean.parseBoolean(properties.getProperty("server.log", "false"));
        } catch (IOException e) {
            log("Error loading logging config, using default (false)");
            return false;
        }
    }

    public static void log(String message) {
        if (loggingEnabled) {
            logger.info(message);
        }
        System.out.println(message);
    }

    public static void main (String[] args) throws IOException {
        int port = loadPortFromConfig(); // порт на котором сервер слушает подключения
        boolean logEnabled = loadLoggingFromConfig();
        ChatServer server = new ChatServer(port, logEnabled);
        server.start();
    }
}
```
отвечает за прослушивание порта сервера, обработку подключений клиентов, 
управление логированием и взаимодействие с конфигурационным файлом.

#### 1.4.1.1 поля
```java
private static final Logger logger = LogManager.getLogger(ChatServer.class.getName());
private static boolean loggingEnabled;
private ServerSocket serverSocket;
private static int activeClients = 0;
```
логгер для записи события происходящих на стороне сервера.  
флаг, определяющий включено ли логгирование.  
сокет сервера для просилушивания входящих подключений.  
счетчик авктивных клиентов.

#### 1.4.1.2 конструктор `ChatServer`
```java
public ChatServer(int port, boolean loggingEnabled) {
    this.loggingEnabled = loggingEnabled;
    try {
        this.serverSocket = new ServerSocket(port);
        // очистка файла логгирования
        new FileWriter("task5\\src\\main\\resources\\serverlog.txt", false).close();
        log("Server started on port: " + port);
    } catch (IOException e) {
        log("Error starting server: " + e.getMessage());
        System.exit(0);
    }
}
```
создает `ServerSocket` на указанном порту.  
очситка файла логгирования при запуске сервера (смотрел [здесь](https://www.baeldung.com/java-delete-file-contents)).
логирует сообщение об успешном старте или ошибке.

#### 1.4.1.3 метод `start`
```java
public void start() {
    try {
        while (!serverSocket.isClosed()) {
            Socket clientSocket = serverSocket.accept();
            ++activeClients;
            log("A new client has connected! Active clients: " + activeClients);
            ClientHandler handler = new ClientHandler(clientSocket);
            Thread thread = new Thread(handler);
            thread.start();
        }
    } catch (IOException e) {
        log("Error starting server: " + e.getMessage());
    }
}
```
здесь в бесконечном цикле принимаются подключения клиентов.  
для каждого клиентского подключения создаются обработчики `ClientHandler`и они запускаются в отдельных потоках.  

#### 1.4.1.4 метод `clientDisconnected`
```java
public static void clientDisconnected() {
    --activeClients;
    log("A client has disconnected. Active clients: " + activeClients);
    if (activeClients == 0) {
        log("No clients connected. Server shutting down...");
        System.exit(0);
    }
}
```
этим методом `ClientHandler` уведомляет сервер о выходе клиента из чата.  
здесь декременируется счетчик активных клиентов и если счетчик равен нулю, то сервер закрывается.

#### 1.4.1.5 метод `loadPortFromConfig`
```java
public static int loadPortFromConfig() {
    Properties properties = new Properties();
    try (FileInputStream input = new FileInputStream("task5\\src\\main\\resources\\config.properties")) {
        properties.load(input);
        return Integer.parseInt(properties.getProperty("server.port", "1234"));
    } catch (IOException e) {
        log("Error loading config, using default port 1234");
        return 1234;
    }
}
```
этот метод загружает порт из `config.properties`.  
Он вызывается в классе `ClientGUI` для того чтобы мог быть создан объект класса `ChatClient` 
которому необходимо знать порт сервера для подключения к нему.  
Также вызывается в `ChatServer` для создания серверного сокета с этим портом.



#### 1.4.1.6 метод `loadLoggingFromConfig`
```java
public static boolean loadLoggingFromConfig() {
    Properties properties = new Properties();
    try (FileInputStream input = new FileInputStream("task5/src/main/resources/config.properties")) {
        properties.load(input);
        return Boolean.parseBoolean(properties.getProperty("server.log", "false"));
    } catch (IOException e) {
        log("Error loading logging config, using default (false)");
        return false;
    }
}
```
загрузка флага логирования из `config.properties`.

#### 1.4.1.7 метод `log`
```java
public static void log(String message) {
    if (loggingEnabled) {
        logger.info(message);
    }
    System.out.println(message);
}
```
Специальный метод чтобы осуществлять вывод сообщений и в консоль и в 
файл логирования `serverlog.txt`.  

#### 1.4.1.8 метод `main`
```java
public static void main (String[] args) throws IOException {
    int port = loadPortFromConfig(); // порт на котором сервер слушает подключения
    boolean logEnabled = loadLoggingFromConfig();
    ChatServer server = new ChatServer(port, logEnabled);
    server.start();
}
```
этот метод - ТОЧКА ВХОДА В СЕРВЕРНОЕ ПРИЛОЖЕНИЕ.
- `loadPortFromConfig()` читает номер порта из конфигурационного файла, на котором сервер будет слушать подключения. 
- `loadLoggingFromConfig()` - читает флаг для определения, включено ли логирование.
- на основе загруженных параметров создаётся новый объект `ChatServer`, которому передаются порт и состояние логирования.
- и уже затем вызывается метод `start()` у созданного объекта, который запускает основной цикл сервера 
для принятия входящих подключений от клиентов.

Пример работы запуска сервера:
1. в методе `main()` загружаются настройки:  
```java
int port = loadPortFromConfig();
boolean logEnabled = loadLoggingFromConfig();
```
Создается экземпляр `ChatServer` и запускается метод `start()`.
2. подключение клиента:    
Клиент подключается через `Socket clientSocket = serverSocket.accept()`.  
Сервер увеличивает счетчик `activeClients` и логирует событие.
3. обработка отключения:  
При отключении клиента вызывается `clientDisconnected()`.  
Если активных клиентов нет, сервер завершает работу (`System.exit(0);`).



### 1.4.2 `ClientHandler` class
```java
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
```
обрабатывает взаимодействие сервера с конкретным клиентом. 
Каждый экземпляр этого класса работает в отдельном потоке

#### 1.4.2.1 поля
```java
public static ArrayList<ClientHandler> clients = new ArrayList<>();
private Socket socket; // to establish connection between server and client
// socket represents a connection between server(clienthandler) and client
private ObjectInputStream in; // to read messages sent from client
private ObjectOutputStream out; // to send messages to client
private String userName;
```
список всех активных клиентов.
сокет для связи с клиентом.
поток для чтения сериализованных объектов (сообщений) от клиента.
поток для отправки сериализованных объектов клиенту.
имя пользователя, полученное при регистрации.

#### 1.4.2.2 конструктор `ClientHandler`
```java
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
```
Инициализирует потоки `ObjectInputStream` и `ObjectOutputStream`.  
Первым сообщением ожидается `LOGIN`, из которого извлекается имя пользователя.  
Клиент добавляется в общий список `clients`, и рассылается уведомление о подключении нового.

#### 1.4.2.3 метод `run`
```java
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
```
в бесконечном цикле чита сообщения от клиента в `ObjectInputStream`.  
Обрабатывает типы сообщений:
- LOGOUT - рассылает уведомление о выходе и завершает цикл.
- LIST - отправляет текущий список пользователей ТОЛЬКО запросившему клиенту.
- MESSAGE - рассылает сообщение всем участникам чата методом `broadcastMessage`.

#### 1.4.2.4 метод `broadcastMessage`
```java
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
```
то логично свое же сообщение я не получаю, его получают все остальные.
В случае если отправка не удалась (получено исключение), то закрываем 
проблемное соединение методом `closeAll`.

#### 1.4.2.5 метод `closeAll`
```java
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
```
название стоит исправить ибо подразумевается удаление клиента из списка `clients` (значит не получает сообщений).  
Закрываем сокет (значит соединение с клиентом закрывается) и потоки ввода-вывода.  



### 1.5 `client` package
### 1.5.1 `ChatClient` class
```java
public class ChatClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String userName;
    private ClientGUI gui;

    public ChatClient(String userName, int port, String serverHost, ClientGUI gui) {
        try {
            socket = new Socket(serverHost, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            this.userName = userName;
            this.gui = gui;

            // отправляем сообщение регистрации
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
            out.flush(); // записывает в поток отдельный объект
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
                        System.out.println(msg.toString());
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
```
этот класс -  клиентская часть чата. 
Он отвечает за подключение к серверу, отправку и прием сообщений от сервера, взаимодействие с GUI юзера.

#### 1.5.1.1 поля
```java
private Socket socket;
private ObjectOutputStream out;
private ObjectInputStream in;
private String userName;
private ClientGUI gui;
```
сокет для подключения к серверу.  
поток для отправки сериализованных объектов (объекттов Message).  
поток для чтения.  
имя пользователя - указано при регистрации пользователя в `JOptionPane`.  
GUI - область чата.

#### 1.5.1.2 конструктор `ChatClient`
```java
public ChatClient(String userName, int port, String serverHost, ClientGUI gui) {
    try {
        socket = new Socket(serverHost, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        this.userName = userName;
        this.gui = gui;

        // отправляем сообщение регистрации
        Message loginMsg = new Message(Type.LOGIN, userName, "joined the chat");
        sendMessage(loginMsg);
    } catch (IOException e) {
        System.out.println("Unable to connect to server: " + e.getMessage());
        System.exit(0);
    }
}
```
Устанавливает соединение с сервером через `Socket`.  
Инициализирует потоки для сериализации/десериализации.  
Отправляет сообщение `LOGIN` с именем пользователя для регистрации. 
Оно будет выведено всем пользователям кроме самого отправителя.

#### 1.5.1.3 `sendMessage`
```java
public void sendMessage(Message msg) {
    try {
        out.writeObject(msg); // записывает в поток отдельный объект
        out.flush(); //очищает буфер и сбрасывает его содержимое в выходной поток
    } catch (IOException e) {
        closeAll();
    }
}
```

#### 1.5.1.4 `listenForMessages`
```java
public void listenForMessages() {
    new Thread(() -> {
        try {
            Object obj;
            while ((obj = in.readObject()) != null) {
                if (obj instanceof Message) {
                    Message msg = (Message) obj;
                    gui.appendMessage(msg.toString());
                    System.out.println(msg.toString());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving messages: " + e.getMessage());
        } finally {
            closeAll();
        }
    }).start();
}
```
Этот метод работает в отдельном потоке, чтобы не блокировать GUI.
он преобразует полученный от `in.readObject()` объект в строку 
и передает в интерфейс `GUI`.

#### 1.5.1.5 `closeAll`
```java
public void closeAll() {
    try {
        if (socket != null) socket.close();
        if (out != null) out.close();
        if (in != null) in.close();
    } catch (IOException e) {
        System.out.println("Error closing socket: " + e.getMessage());
    }
}
```
Закрывает сокет и потоки при ошибках или выходе из чата клиентом.

#### 1.5.1.6 `loadHostFromConfig`
```java
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
```
для клиента нужна загрузка хоста из конфига для содания сокета, а значит 
и подключения к серверу. 



### 1.5.2 `ClientGUI` class
```java
public class ClientGUI {
    private ChatClient client;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    public ClientGUI(String userName, int port, String serverHost) {
        client = new ChatClient(userName, port, serverHost, this);

        // окно чата
        frame = new JFrame("Chat - " + userName);
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
        sendButton = new JButton("Send");

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
        sendButton.addActionListener(e -> sendMessage());

        // отправка сообщений по Enter (не по кнопке на панели, а кнопке клавиатуры Enter)
        messageField.addActionListener(e -> sendMessage());

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
                appendMessage("You have left the chat.");
                client.closeAll();
                System.exit(0);
            }
            Message msg = new Message(Type.MESSAGE, client.getUserName(), message);
            client.sendMessage(msg);
            appendMessage("You: " + message); // в свой чат пишем от лица себя
            messageField.setText(""); // после отправки сообщения окно пустое
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
```
класс реализует графический интерфейс клиентской части чата.

#### 1.5.2.1 поля
```java
private ChatClient client;
private JFrame frame;
private JTextArea chatArea;
private JTextField messageField;
private JButton sendButton;
```
объект класса `ChatClient` для сетевого взаимодействия с сервером.  
главное окно приложения.  
текстовая область для отображения в ней переписки  
поля для ввода сообщений пользователя  
кнопка отправки сообщения

#### 1.5.2.2 конструктор `ClientGUI`
```java
public ClientGUI(String userName, int port, String serverHost) {
        client = new ChatClient(userName, port, serverHost, this);

        // окно чата
        frame = new JFrame("Chat - " + userName);
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
        sendButton = new JButton("Send");

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // сообщения отправляются по кнопке на панели
        sendButton.addActionListener(_ -> sendMessage());

        // отправка сообщений по Enter (не по кнопке на панели, а кнопке клавиатуры Enter)
        messageField.addActionListener(_ -> sendMessage());

        frame.setVisible(true);

        client.listenForMessages();
    }
```
создаем экземпляр `ChatClient` для подключения к серверу.  
настраиваем окно приложения: заголовок, размер, расположение элементов.  
добавляем область чата с прокруткой для отображения переписки.  
размещаем поле ввода и кнопку отправки в нижней части окна.  
регистрируем обработчики событий для кнопки `sendButton` и клавиши Enter.  
запускаем поток `listenForMessages()` для прослушивания входящих сообщений с сервера.

#### 1.5.2.3 метод `sendMessage`
```java
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
//            appendMessage("You have left the chat.");
            client.closeAll();
            System.exit(0);
        }
        Message msg = new Message(Type.MESSAGE, client.getUserName(), message);
        client.sendMessage(msg);
        appendMessage("You: " + message); // в свой чат пишем от лица себя
        messageField.setText(""); // после отправки сообщения окно пустое
    }
}
```
если напишу в `messageField` команды `/list` или `/exit`, то их 
обрабатывает этот метод:
 - если `/list` - отправляем запрос на получение списка пользователей
 - если `/exit` - отправляем запрос на выход пользователя
 - в противном случае - отправляем обычное сообщение

#### 1.5.2.4 метод `appendMessage`
```java
public void appendMessage(String message) {
    chatArea.append(message + "\n");
}
```
в область свою собственную область чата добавить сообщение.

#### 1.5.2.5 метод `main`
```java
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
```
этот метод - ТОЧКА ВХОДА В КЛИЕНТСКОЕ ПРИЛОЖЕНИЕ. 
- получаю имя пользователя
- Если пользователь ввёл имя (userName != null), метод продолжает 
выполнение. Иначе выводится сообщение об ошибке, и приложение завершает работу.
- загружаю порт из конфигурационного файла
- загружаю хост из конфигурационного файла
- создаю экземпляр `ClientGUI` для запуска приложения. 
А уже этот экземпляр отвечает за построение Swing-интерфейса и установливает 
соединения с сервером.

## 2 XML сообщения
немного теории:
DOM (Document Object Model) - древовидное представление XML-документа в памяти. Каждый элемент XML становится узлом дерева.
пример: 
```xml
<command name="logout">
  <session>abc123</session>
</command>
```
в DOM выглядит так: 
```
Document
└── Element (command)
    ├── Attribute (name="logout")
    └── Element (session)
        └── Text (abc123)
```
  
Как устроены файлы XML?
- **Элементы**. Это основные строительные блоки XML-документа. 
Они заключены в теги — например, `<book>…</book>` — и могут содержать текст, атрибуты и другие элементы.
- **Корневой элемент**.
  Единственный элемент верхнего уровня, который содержит все остальные элементы.  
- **Атрибуты**. Это дополнительные данные, которые можно добавить к 
элементам, чтобы более точно указать их характеристики. 
Они записываются внутри открывающего тега и позволяют добавлять дополнительную
информацию без создания новых элементов. 
Например, в элементе `<book genre="учебник">` атрибут `genre` указывает на жанр книги, что помогает более точно описать её содержание.
- **Текстовое содержимое**. К тексту относится всё, что находится между 
открывающим и закрывающим тегами элемента. 
Например, `<title>XML для начинающих</title>`.

? `private DataOutputStream dataOut` и `private DataInputStream dataIn` - 
почему во второй версии программы мы выбрали такие объекты вместо 
прежних `ObjectInputStream` и `ObjectOutputStream` для десериализации и 
сериализации соответственно?  

Ответ: `ObjectInputStream`/`ObjectOutputStream` предназначены для 
сериализации Java-объектов, а не для работы с XML. 
Если попытаться записать XML-строку через `ObjectOutputStream`, 
она будет сериализована как объект `String`, что нарушит протокол.
Также для XML сообщений по заданию используется протокол:  
`[4 байта длины сообщения][XML данные]`.

про эти 2 класса читай [здесь](https://metanit.com/java/tutorial/6.7.php).  
если вкратце то:  
`DataOutputStream` -  поток вывода и предназначен для записи данных примитивных типов, таких, как `int`.  
пример использования: 
```java
dataOut.writeInt(data.length); //  записывает в поток целочисленное значение int
dataOut.write(data); // записывает все байты в выходной поток
dataOut.flush(); // сбрасывает буфер выходного потока (который строкой выше заполнил)
```
`DataInputStream` действует противоположным образом - он считывает из потока данные примитивных типов.


-------------------------------------------------
Логика программы в пекедже `xml` аналогична, теперь вместо отправки объектов 
кастомного класса `Message` используются объекты `Document` и `Element`.  

Наглядный пример из `ClientGUI` метода `SendMessage`:  

как было в 1 варианте лабы:
```java
if (message.equalsIgnoreCase("/list")) {
    Message listMsg = new Message(Message.Type.LIST, client.getUserName(), "");
    client.sendMessage(listMsg);
    messageField.setText("");
    return;
}
```

как стало во втором:  
```java
if (message.equalsIgnoreCase("/list")) {
    try {
        /* XML-команда list:
         <command name=”list”>
            <session>UNIQUE_SESSION_ID</session>
         </command>
         */
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element commandElem = doc.createElement("command");
        commandElem.setAttribute("name", "list");
        Element sessionElem = doc.createElement("session");
        sessionElem.setTextContent(client.getSessionId());
        commandElem.appendChild(sessionElem);
        doc.appendChild(commandElem);
        
        client.sendXMLCommand(doc);
    } catch (Exception e) {
        e.printStackTrace();
    }
    messageField.setText("");
    return;
}
```
то есть теперь логика такая: приведу на примере команды `exit`:
1. юзер ввел в поле `messageField` команду `/exit`:
```java
if (message.equalsIgnoreCase("/exit")) {
    try {
        // XML-команда logout: <command name=”logout”><session>UNIQUE_SESSION_ID</session></command>
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element commandElem = doc.createElement("command");
        commandElem.setAttribute("name", "logout");
        Element sessionElem = doc.createElement("session");
        sessionElem.setTextContent(client.getSessionId());
        commandElem.appendChild(sessionElem);
        doc.appendChild(commandElem);
        
        client.sendXMLCommand(doc);
    } catch (Exception e) {
        e.printStackTrace();
    }
//    client.closeAll();
//    System.exit(0);
}
```
собирается XML такое сообщение для сервера: 
```xml
<command name=”logout”>
    <session>UNIQUE_SESSION_ID</session>
</command>
```
то есть создаю элемент `command` с атрибутом `name="logout"` и элемент `session` с содержимым `UNIQUE_SESSION_ID`.  

Это сообщение отправляю функцией `sendXMLCommand` в сервер.
Надо отдельно оговорить как работает функция `writeXMLMessage`:
```java
private void writeXMLMessage(Document doc) throws Exception {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(doc), new StreamResult(writer));
    byte[] data = writer.toString().getBytes(StandardCharsets.UTF_8);
    
    dataOut.writeInt(data.length);
    dataOut.write(data);
    dataOut.flush();
}
```
1.1  Сначала функция использует объект `Transformer` 
(из пакета `javax.xml.transform`) для преобразования XML-документа 
(объект `Document`) в текстовое представление.
   - Создаётся `Transformer` с помощью `TransformerFactory.newInstance().newTransformer()`.
   - Далее создаётся `StringWriter` – поток, записывающий символы в строку.
   - Метод `transformer.transform(new DOMSource(doc), new StreamResult(writer))` 
   берёт XML-документ (DOM-дерево) и записывает его в объект `StringWriter` 
   в виде текста.  

1.2 После получения строки с XML-данными вызывается метод `toString()` у 
`StringWriter`, затем эта строка преобразуется в массив байт с 
использованием кодировки `UTF-8`. Это нужно, потому что в сети 
передаются именно байты.  

1.3
   - записываем длину полученного массива байт. 
Это важно для приемной стороны, чтобы знать, сколько байт нужно
прочитать, прежде чем завершится документ.  
   - После записи длины вызывается `dataOut.write(data)`, 
который записывает все байты XML-строки в выходной поток.
   - Вызывается `dataOut.flush()`, чтобы обеспечить немедленную 
отправку всех накопившихся в буфере данных по потоку.


2. теперь это сообщение будет поймано `clienthandler`-ом и 
считаем значение у атрибута `name` в элементе `command` - это `logout`.  
создаю серверное сообщение сообщающее об успехе: 
```xml
<success>
</success>
```
и это сообщение отправляю обратно клиенту:
```java
writeXMLMessage(successDoc);
```
3. клинет `ChatClient` в бесконечном цикле `listenForMessages` считает 
сообщение об успехе (сначала считал название корневого элемента и сравнил с
сообщением `success`) и если это так, то так как никаких `listusers` нам
не отправлялось от сервера, то делается следующее:
```java
gui.appendMessage("SERVER: Logout successful.");
closeAll();
System.exit(0);
```
первое сообщение является рудиментарным ибо я его вижу чуть-чуть и окно с 
ним закрывается - ура выполнен выход (пиздец блять).

описание метода чтения XML-сообщения
```java
public Document readXMLMessage() throws Exception {
    // читает первые 4 байта из входного потока (DataInputStream), интерпретируя их как целое число (int)
   int length = dataIn.readInt();
   // создается массив байтов размером length
   byte[] data = new byte[length];
   // читает ровно length байтов из потока и сохраняет их в массив data
   dataIn.readFully(data);
   // создается поток ввода на основе массива байтов data
    // зачем? парсер XML (DocumentBuilder) требует InputStream для чтения данных
   ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
   // создается фабрика для получения парсера XML
   DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
   /*
        что происходит:
            newDocumentBuilder() — создает объект DocumentBuilder для парсинга
            parse(byteStream) — преобразует XML-данные из потока в объект Document
    */
   return factory.newDocumentBuilder().parse(byteStream);
}
```
### акпкп