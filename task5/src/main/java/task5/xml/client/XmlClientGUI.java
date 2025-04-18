package task5.xml.client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.awt.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static task5.java_serialize.client.ChatClient.loadHostFromConfig;
import static task5.java_serialize.server.ChatServer.loadPortFromConfig;

public class XmlClientGUI {
    private final XmlChatClient client;
    private final JTextArea chatArea;
    private final JTextField messageField;

    public XmlClientGUI(String userName, int port, String serverHost) {
        client = new XmlChatClient(userName, port, serverHost, this);

        // окно чата
        JFrame frame = new JFrame("XML Chat - " + userName);
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
        JButton sendButton = new JButton("Send");

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

    public void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) return;

        if (message.equalsIgnoreCase("/list")) {
            try {
                // XML-команда list:<command name=”list”><session>UNIQUE_SESSION_ID</session></command>
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
        } else if (message.equalsIgnoreCase("/exit")) {
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
//                client.closeAll();
//                System.exit(0);
        } else if (message.toLowerCase().startsWith("/whisper")) {
            try {
                // message = ["/whisper", "TARGET", "MESSAGE"]
                /* XML-команда для секретной переписки с одним клиентом:
                <command name=”whisper”>
                    <session>UNIQUE_SESSION_ID</session>
                    <target>USERNAME</target>
                    <message>MESSAGE</message>
                </command>
                 */
                String[] parts = message.split("\\s+", 3);
                if (parts.length < 3) {
                    System.out.println("Invalid command: /whisper <user> <message>");
                    return;
                }
                String target = parts[1];
                String body = parts[2];
                System.out.println("Whispering to " + target + ": " + body);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.newDocument();

                Element commandElem = doc.createElement("command");
                commandElem.setAttribute("name", "whisper");
                Element sessionElem = doc.createElement("session");
                sessionElem.setTextContent(client.getSessionId());
                commandElem.appendChild(sessionElem);

                Element targetElem = doc.createElement("target");
                targetElem.setTextContent(target);
                commandElem.appendChild(targetElem);

                Element messageElem = doc.createElement("message");
                messageElem.setTextContent(body);
                commandElem.appendChild(messageElem);

                doc.appendChild(commandElem);
                appendMessage("[whisper]: " + body + " to " + target);

                client.sendXMLCommand(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageField.setText("");
            return;
        }
        try {
            /* XML-команда отправки обычного сообщения от клиента к серверу:
             <command name=”message”>
                <message>MESSAGE</message>
                <session>UNIQUE_SESSION_ID</session>
             </command>
             */
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // создание билдера для XML
            DocumentBuilder builder = factory.newDocumentBuilder(); // создание документа с помощью билдера
            Document doc = builder.newDocument(); // создание нового документа
            Element commandElem = doc.createElement("command");
            commandElem.setAttribute("name", "message");
            Element messageElem = doc.createElement("message");
            messageElem.setTextContent(message);
            Element sessionElem = doc.createElement("session");
            sessionElem.setTextContent(client.getSessionId());
            commandElem.appendChild(messageElem);
            commandElem.appendChild(sessionElem);
            doc.appendChild(commandElem);

            client.sendXMLCommand(doc);
            appendMessage("You: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        messageField.setText("");
    }

    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

    public static void main(String[] args) {
        String userName = JOptionPane.showInputDialog("Enter your username for the XML chat:");
        if (userName != null) {
            int port = loadPortFromConfig();
            String serverHost = loadHostFromConfig();
            new XmlClientGUI(userName, port, serverHost);
        } else {
            System.out.println("Name can't be null...");
            System.exit(0);
        }
    }
}