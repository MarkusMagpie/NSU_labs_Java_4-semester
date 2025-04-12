package task5.java_serialize;

import java.io.Serializable;

public record Message(
        Type type,
        String sender,
        String content
) implements Serializable {
    public enum Type {
        LOGIN, // регистрация юзера
        LOGOUT,
        MESSAGE, // обычное сообщение
        LIST, // запрос списка пользователей
        SYSTEM // системные сообщения (подкл/откл)
    }

    @Override
    public String toString() {
        if (type == Type.MESSAGE || type == Type.SYSTEM)
            return sender + ": " + content;
        else
            return content;
    }
}