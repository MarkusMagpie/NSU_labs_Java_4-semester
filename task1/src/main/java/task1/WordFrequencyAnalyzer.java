package task1;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class WordFrequencyAnalyzer {
    private static final Logger logger = LogManager.getLogger(Main.class);

    private final Map<String, Integer> word_frequency = new HashMap<>();
    private int word_count;

    // теперь эта функция - анализ частот слов в ОДНОМ файле
    public Map<String, Integer> analyzeFile(String filename) throws IOException {
        Reader reader = new InputStreamReader(new FileInputStream(filename));
        StringBuilder sb = new StringBuilder(); // для построения слов и записи слов в word_frequency
        int current_char;

        // читаем reader посимвольно
        while ((current_char = reader.read()) != -1) {
            char c = (char) current_char;

            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
            } else if (!sb.isEmpty()) { // если символ - разделитель, то увеличиваем на 1 частоту этого слова в Map
                String word = sb.toString().toLowerCase(); // переводим полученную строку из StringBuilder в нижний регистр
                word_frequency.put(word, word_frequency.getOrDefault(word, 0) + 1);
                word_count++;
                sb.setLength(0); // очищаем StringBuilder

                logger.info("Слово: " + word + ", частота: " + word_frequency.get(word));
            }
        }

        if (!sb.isEmpty()) {
            String word = sb.toString().toLowerCase();
            word_frequency.put(word, word_frequency.getOrDefault(word, 0) + 1);
            word_count++;

            logger.info("Слово: " + word + ", частота: " + word_frequency.get(word));
        }

        logger.info("Всего слов: " + word_count);
        return word_frequency;
    }

    public void writeToCSV(Map<String, Integer> word_frequency, String output_file) {
        // создаем список из Map.Entry<String, Integer> и сортируем его в порядке убывания
        List<Map.Entry<String, Integer>> sorted_words = new ArrayList<>(word_frequency.entrySet()); // entrySet возвращает Set всех элементов Map (пар)
//        sorted_words.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        sorted_words.sort((e1, e2) -> {
            Integer frequency1 = e1.getValue();
            Integer frequency2 = e2.getValue();
            return frequency2.compareTo(frequency1);
        });

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(output_file))) {
            writer.println("Слово,Частота,Частота (%)");

            // отсортированный лист sorted_words записываем в выходной файл CSV
            for (Map.Entry<String, Integer> entry : sorted_words) {
                String word = entry.getKey();
                int frequency = entry.getValue();
                double percentage = (double) frequency / word_count * 100;
                writer.println(word + "," + frequency + "," + String.format("%.0f", percentage) + "%");
            }
        } catch (IOException e) {
            System.out.println("Error while writing to CSV file: " + e.getMessage());
            logger.error("Ошибка при записи в CSV-файл: " + e.getMessage());
        }
    }
}
