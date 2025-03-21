package task2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        ExecutionContext context = new ExecutionContext();
        // считываем команды либо из файла args[0] либо из стандартного ввода
        try (BufferedReader reader = args.length > 0
                ? new BufferedReader(new FileReader(args[0]))
                : new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // удалить пробелы в начале и конце строки
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(" ");
                String command_name = parts[0].toUpperCase(); // первый элемент строки - всегда команда
                String[] command_args = Arrays.copyOfRange(parts, 1, parts.length); // остальные - аргументы команды

                logger.log(Level.INFO, "Command received: " + command_name + " " + Arrays.toString(command_args));
                try {
                    Command command = CommandFactory.CreateCommand(command_name);
                    command.Execute(context, command_args);
                    logger.log(Level.INFO, "Command executed: " + command_name + " " + Arrays.toString(command_args));
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error while executing command: " + command_name, e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while reading input file", e);
        }
    }
}

// проверка на тип данных во входящем файле define
// делать exception при выходе за диапозон
// логика ошибок при делении на ноль и sqrt < 0
