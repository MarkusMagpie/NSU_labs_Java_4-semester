package task2;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class CommandFactory {
    private static final Map<String, String> command_map = new HashMap<>();
    //  сопоставляет имя команды с полным именем класса, реализующего эту команду
    //  PUSH -> task2.commands.PushCommand

    CommandFactory() {
        try (InputStream input = CommandFactory.class.getResourceAsStream("/commands.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            for (String name : properties.stringPropertyNames()) {
                command_map.put(name, properties.getProperty(name));
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке конфигурационного файла", e);
        }
    }

    public static Command CreateCommand(String name) throws Exception {
        String class_name = command_map.get(name);
        if (class_name == null) {
            throw new IllegalArgumentException("Неизвестная команда: " + name);
        }
        return (Command) Class.forName(class_name).getDeclaredConstructor().newInstance();
    }
}
