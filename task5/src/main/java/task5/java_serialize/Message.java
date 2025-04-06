package task5.java_serialize;

import java.io.Serializable;

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