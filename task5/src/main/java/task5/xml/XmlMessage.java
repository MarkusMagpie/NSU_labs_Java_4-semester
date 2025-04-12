package task5.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlMessage {
    public enum Type {
        LOGIN,
        LOGOUT,
        MESSAGE,
        LIST,
        USER_LOGIN,
        USER_LOGOUT,
        SUCCESS,
        ERROR
    }

    private final Type type;
    private final String sender;
    private final String content;
    private final String sessionId;

    public XmlMessage(Type type, String sender, String content, String sessionId) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.sessionId = sessionId;
    }

    public XmlMessage(Type type, String content, String sessionId) {
        this(type, null, content, sessionId);
    }

    // Генерация XML из объекта
    public Document toXmlDocument() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();

        switch (type) {
            case LOGIN:
                Element command = doc.createElement("command");
                command.setAttribute("name", "login");
                Element name = doc.createElement("name");
                name.setTextContent(sender);
                Element typeElem = doc.createElement("type");
                typeElem.setTextContent("CHAT_CLIENT_XML");
                command.appendChild(name);
                command.appendChild(typeElem);
                doc.appendChild(command);
                break;

            case MESSAGE:
                Element msgCommand = doc.createElement("command");
                msgCommand.setAttribute("name", "message");
                Element message = doc.createElement("message");
                message.setTextContent(content);
                Element session = doc.createElement("session");
                session.setTextContent(sessionId);
                msgCommand.appendChild(message);
                msgCommand.appendChild(session);
                doc.appendChild(msgCommand);
                break;

            case USER_LOGIN:
        }

        return doc;
    }

    // Парсинг XML в объект
    public static XmlMessage fromXml(Document doc) {
        Element root = doc.getDocumentElement();
        String tagName = root.getTagName();

        if ("event".equals(tagName)) {
            String eventType = root.getAttribute("name");
            switch (eventType) {
                case "message":
                    String msgContent = root.getElementsByTagName("message").item(0).getTextContent();
                    String sender = root.getElementsByTagName("name").item(0).getTextContent();
                    return new XmlMessage(Type.MESSAGE, sender, msgContent, null);
                case "userlogin":
                case "userlogout":
                    String user = root.getElementsByTagName("name").item(0).getTextContent();
                    return new XmlMessage(
                            "userlogin".equals(eventType) ? Type.USER_LOGIN : Type.USER_LOGOUT,
                            user, null, null
                    );
            }
        }

        // Обработка других типов...
        return null;
    }

    // Геттеры
    public Type getType() { return type; }
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public String getSessionId() { return sessionId; }
}