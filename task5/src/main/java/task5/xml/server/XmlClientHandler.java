package task5.xml.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import task5.xml.client.XmlChatClient;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

public class XmlClientHandler implements Runnable {
    public static ArrayList<XmlClientHandler> clients = new ArrayList<>();
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private String userName;

    public XmlClientHandler(Socket socket) {
        try {
            this.socket = socket;
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());

            // 1 полученный объект - сообщение регистрации login
            Document doc = readXMLMessage();
            Element root = doc.getDocumentElement();
            if ("command".equals(root.getTagName()) &&
                    "login".equals(root.getAttribute("name"))) {
                // извлекаем имя пользователя и тип клиента (для примера тип не обрабатываем)
                userName = root.getElementsByTagName("name").item(0).getTextContent();
                // генерация сессионного идентификатора
                String sessionId = UUID.randomUUID().toString(); // уникальный идентификатор сессии - UUID
                clients.add(this);

                // сначала отправить <success> а уже после него выдать клиенту все накопленные события из истории
                // отправляем ответ успешного логина с сессионным id:
                /*
                <success><session>UNIQUE_SESSION_ID</session></success>
                 */
                Document successDoc = createDocument();
                Element successElem = successDoc.createElement("success");
                Element sessionElem = successDoc.createElement("session");
                sessionElem.setTextContent(sessionId);
                successElem.appendChild(sessionElem);
                successDoc.appendChild(successElem);

                // 1 - отправляю ответ на логин (ответ - success) НОВОМУ клиенту
                writeXMLMessage(successDoc);

                // 2 - отправляю всю историю новому клиенту
                for (Document historyDoc : XmlChatServer.messageHistory) {
                    writeXMLMessage(historyDoc);
                }

                // 3 - подготовка сообщения о входе нового пользователя для других
                Document userLoginDoc = createDocument();
                Element eventElem = userLoginDoc.createElement("event");
                eventElem.setAttribute("name", "userlogin");
                Element nameElem = userLoginDoc.createElement("name");
                nameElem.setTextContent(userName);
                eventElem.appendChild(nameElem);
                userLoginDoc.appendChild(eventElem);

                // 4 - отправка сообщения НОВОМУ клиенту
                writeXMLMessage(userLoginDoc);

                // 5 - добавка в историю и рассылка ВСЕМ остальным клиентам
                XmlChatServer.addMessageToHistory(userLoginDoc);
                broadcastXML(userLoginDoc);
            } else {
                /* ответ ошибки логина
                <error><message>REASON</message></error>
                 */
                Document errorDoc = createDocument();
                Element errorElem = errorDoc.createElement("error");
                Element messageElem = errorDoc.createElement("message");
                messageElem.setTextContent("Login error");
                errorElem.appendChild(messageElem);
                errorDoc.appendChild(errorElem);

                writeXMLMessage(errorDoc);
            }
        } catch (Exception e) {
            closeAll();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Document doc = readXMLMessage();
                if (doc == null) break;

                Element root = doc.getDocumentElement();
                String command = root.getAttribute("name");
                switch (command) {
                    case "message": {
                        // пример: <command name="message"><message>TEXT</message><session>...</session></command>
                        root.getElementsByTagName("session").item(0);
                        String text = root.getElementsByTagName("message").item(0).getTextContent();

                        // формирование XML-ответа для всех клиентов
                        Document eventDoc = createDocument();
                        Element eventElem = eventDoc.createElement("event");
                        eventElem.setAttribute("name", "message");
                        Element msgElem = eventDoc.createElement("message");
                        msgElem.setTextContent(text);
                        Element fromElem = eventDoc.createElement("name");
                        fromElem.setTextContent(userName);
                        eventElem.appendChild(msgElem);
                        eventElem.appendChild(fromElem);
                        eventDoc.appendChild(eventElem);

                        XmlChatServer.addMessageToHistory(eventDoc);
                        broadcastXML(eventDoc);
                        break;
                    }
                    case "list": {
                        // <command name="list"><session>...</session></command>
                        // формируем ответ, содержащий список пользователей
                        Document successDoc = createDocument();
                        Element successElem = successDoc.createElement("success");
                        Element listUsersElem = successDoc.createElement("listusers");
                        for (XmlClientHandler client : clients) {
                            Element userElem = successDoc.createElement("user");
                            Element nameElem = successDoc.createElement("name");
                            nameElem.setTextContent(client.userName);
                            Element typeElem = successDoc.createElement("type");
                            // можно указать тип клиента
                            typeElem.setTextContent("CHAT_CLIENT_XML");
                            userElem.appendChild(nameElem);
                            userElem.appendChild(typeElem);
                            listUsersElem.appendChild(userElem);
                        }
                        successElem.appendChild(listUsersElem);
                        successDoc.appendChild(successElem);

                        writeXMLMessage(successDoc); // отправка только самому себе
                        break;
                    }
                    case "logout": {
                        // <command name="logout"><session>...</session></command>
                        // отправляем успешный ответ: <success></success>
                        Document successDoc = createDocument();
                        Element successElem = successDoc.createElement("success");
                        successDoc.appendChild(successElem);

                        writeXMLMessage(successDoc);

                        // сообщаем другим клиентам, что клиент вышел
                        // <event name=”userlogout”><name>USER_NAME</name></event >
                        Document eventDoc = createDocument();
                        Element eventElem = eventDoc.createElement("event");
                        eventElem.setAttribute("name", "userlogout");
                        Element nameElem = eventDoc.createElement("name");
                        nameElem.setTextContent(userName);
                        eventElem.appendChild(nameElem);
                        eventDoc.appendChild(eventElem);

                        XmlChatServer.addMessageToHistory(eventDoc);
                        broadcastXML(eventDoc);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error in XML client handler: " + e.getMessage());
            System.out.println("Disconnected client: " + userName);
        } finally {
            closeAll();
            XmlChatServer.clientDisconnected();
        }
    }

    // чтение XML-сообщения: сначала 4 байта с длиной, затем само сообщение
    public Document readXMLMessage() throws Exception {
        int length = dataIn.readInt();
        byte[] data = new byte[length];
        dataIn.readFully(data);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(byteStream);
        return doc;
    }

    // запись XML-сообщения: сначала записывается длина сообщения, затем само сообщение
    public void writeXMLMessage(Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        byte[] data = writer.toString().getBytes(StandardCharsets.UTF_8);

        dataOut.writeInt(data.length);
        dataOut.write(data);
        dataOut.flush();
    }

    // метод для создания нового документа
    public Document createDocument() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.newDocument();
    }

    // рассылка XML сообщения всем клиентам, кроме отправителя
    public void broadcastXML(Document doc) {
        for (XmlClientHandler client : clients) {
            try {
                if (client.userName.equals(this.userName)) {
                    continue;
                }
                client.writeXMLMessage(doc);
            } catch (Exception e) {
                client.closeAll();
            }
        }
    }

    public void closeAll() {
        clients.remove(this);
        try {
            if (socket != null) socket.close();
            if (dataIn != null) dataIn.close();
            if (dataOut != null) dataOut.close();
        } catch (IOException e) {
            System.out.println("Error closing XML connection: " + e.getMessage());
        }
    }
}
