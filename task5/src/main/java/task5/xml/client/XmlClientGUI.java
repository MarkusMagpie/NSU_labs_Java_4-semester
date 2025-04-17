package task5.xml.client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import task5.xml.server.XmlChatServer;

import javax.swing.*;
import java.awt.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
        if (!message.isEmpty()) {
            if (message.equalsIgnoreCase("/list")) {
                try {
                    /* XML-команда list:
                     <command name=”list”><session>UNIQUE_SESSION_ID</session></command>
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
    }

    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

    public static void main(String[] args) {
        String userName = JOptionPane.showInputDialog("Enter your username for the XML chat:");
        if (userName != null) {
            int port = XmlChatServer.loadPortFromConfig();
            String serverHost = XmlChatClient.loadHostFromConfig();
            new XmlClientGUI(userName, port, serverHost);
        } else {
            System.out.println("Name can't be null...");
            System.exit(0);
        }
    }
}