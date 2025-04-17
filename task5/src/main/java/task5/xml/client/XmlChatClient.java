package task5.xml.client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import task5.java_serialize.client.ChatClient; // для функции loadPortFromConfig

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlChatClient {
    private Socket socket;
    private DataOutputStream dataOut;
    private DataInputStream dataIn;
    private String sessionId;
    private XmlClientGUI gui;

    public XmlChatClient(String userName, int port, String serverHost, XmlClientGUI gui) {
        try {
            socket = new Socket(serverHost, port);
            dataOut = new DataOutputStream(socket.getOutputStream());
            dataIn = new DataInputStream(socket.getInputStream());
            this.gui = gui;

            // отправляем сообщение логина
            Document loginDoc = createDocument();
            Element commandElem = loginDoc.createElement("command");
            commandElem.setAttribute("name", "login");
            Element nameElem = loginDoc.createElement("name");
            nameElem.setTextContent(userName);
            commandElem.appendChild(nameElem);

            // добавил тип клиента
            Element typeElem = loginDoc.createElement("type");
            typeElem.setTextContent("CHAT_CLIENT_XML");
            commandElem.appendChild(typeElem);
            loginDoc.appendChild(commandElem);
            writeXMLMessage(loginDoc);

            // жду ответ сервера ("success" или "error")
            Document response = readXMLMessage();
            Element root = response.getDocumentElement();
            if ("success".equals(root.getTagName())) {
                sessionId = root.getElementsByTagName("session").item(0).getTextContent();
                System.out.println("Logged in with session id: " + sessionId);
            } else {
                // сервер вернул ошибку при логине:
                // <error><message>REASON</message></error>
                JOptionPane.showMessageDialog(null, "Login error: " +
                        root.getElementsByTagName("message").item(0).getTextContent());
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("Unable to connect with XML to server: " + e.getMessage());
            System.exit(0);
        }
    }

    // для отправки XML-сообщений
    public void sendXMLCommand(Document doc) {
        try {
            writeXMLMessage(doc);
        } catch (Exception e) {
            System.out.println("Error sending XML command: " + e.getMessage());
            closeAll();
        }
    }

    public void listenForMessages() {
        new Thread(() -> {
            try {
                while (true) {
                    Document doc = readXMLMessage();
                    if (doc == null) break;

                    Element root = doc.getDocumentElement(); // корневой элемент
                    String tagName = root.getTagName(); // название корневого элемента
                    if ("event".equals(tagName)) {
                        // обработка разных событий отправленных от сервера клиенту
                        String eventName = root.getAttribute("name");
                        if ("message".equals(eventName)) {
                            String message = root.getElementsByTagName("message").item(0).getTextContent();
                            String from = root.getElementsByTagName("name").item(0).getTextContent();
                            gui.appendMessage(from + ": " + message);
                        } else if ("whisper".equals(eventName)) {
                            String from = root.getElementsByTagName("from").item(0).getTextContent();
                            String msg  = root.getElementsByTagName("message").item(0).getTextContent();
                            gui.appendMessage("[whisper] " + from + ": " + msg);
                        } else if ("userlogin".equals(eventName)) {
                            String name = root.getElementsByTagName("name").item(0).getTextContent();
                            gui.appendMessage("SERVER: " + name + " joined the chat.");
                        } else if ("userlogout".equals(eventName)) {
                            String name = root.getElementsByTagName("name").item(0).getTextContent();
                            gui.appendMessage("SERVER: " + name + " left the chat.");
                        }
                    }
                    if ("success".equals(tagName)) {
                        // обработка ответов на команды
                        if (root.getElementsByTagName("listusers").getLength() > 0) {
                            // формирование строки со списком пользователей
                            Element listElem = (Element) root.getElementsByTagName("listusers").item(0);
                            StringBuilder users = new StringBuilder("Active users:\n");
                            for (int i = 0; i < listElem.getElementsByTagName("user").getLength(); i++) {
                                Element userElem = (Element) listElem.getElementsByTagName("user").item(i);
                                String name = userElem.getElementsByTagName("name").item(0).getTextContent();
                                String type = userElem.getElementsByTagName("type").item(0).getTextContent();
                                users.append(name).append(" (").append(type).append(")\n");
                            }
                            gui.appendMessage(users.toString().trim());
                        } else {
                            gui.appendMessage("SERVER: Logout successful.");
                            closeAll();
                            System.exit(0);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error receiving XML message: " + e.getMessage());
            } finally {
                closeAll();
            }
        }).start();
    }

    // чтение XML-сообщения аналогично серверной реализации
    public Document readXMLMessage() throws Exception {
        int length = dataIn.readInt();
        byte[] data = new byte[length];
        // https://stackoverflow.com/questions/25897627/datainputstream-read-vs-datainputstream-readfully
        dataIn.readFully(data);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder().parse(byteStream);
    }

    // запись XML-сообщения
    public void writeXMLMessage(Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        byte[] data = writer.toString().getBytes(StandardCharsets.UTF_8);

        dataOut.writeInt(data.length);
        dataOut.write(data);
        dataOut.flush();
    }

    public Document createDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder().newDocument();
    }

    public void closeAll() {
        try {
            if (socket != null) socket.close();
            if (dataIn != null) dataIn.close();
            if (dataOut != null) dataOut.close();
        } catch (IOException e) {
            System.out.println("Error closing XML socket: " + e.getMessage());
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public static String loadHostFromConfig() {
        return ChatClient.loadHostFromConfig();
    }
}
