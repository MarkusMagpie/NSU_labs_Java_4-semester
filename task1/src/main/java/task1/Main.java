package task1;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.Map;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Error: please specify input file name as a single argument.");
            logger.error("Неверное количество аргументов: " + args.length);
            return;
        }

        String input_file = args[0];
        WordFrequencyAnalyzer analyzer = new WordFrequencyAnalyzer();

        try {
            Map<String, Integer> word_frequency = analyzer.analyzeFile(input_file);

            // создание выходного CSV файла
            String output_file = "task1/src/main/resources/output.csv";
            analyzer.writeToCSV(word_frequency, output_file);

            logger.info("CSV-файл создан: " + output_file);
        } catch (IOException e) {
            System.out.println("Error while reading file: " + input_file + ": " + e.getMessage());
            logger.error("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}
