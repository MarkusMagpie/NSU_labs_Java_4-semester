package task1;

import java.io.*;
import java.util.*;

public class WordFrequencyAnalyzer {
    private final HashMap<String, Integer> word_frequency = new HashMap<>();
    private int word_count;

    // теперь эта функция - анализ частот слов в ОДНОМ файле
    public Map<String, Integer> analyzeFile(String filename) throws IOException {
        try (Reader reader = new InputStreamReader(new FileInputStream(filename))) {
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
                }
            }

            if (!sb.isEmpty()) {
                String word = sb.toString().toLowerCase();
                word_frequency.put(word, word_frequency.getOrDefault(word, 0) + 1);
                word_count++;
            }
        }

        return new HashMap<>(word_frequency);
    }

    private void writeToCSV(Map<String, Integer> word_frequency, String output_file) {
        // создаем список из Map.Entry<String, Integer> и сортируем его в порядке убывания
        List<Map.Entry<String, Integer>> sorted_words = new ArrayList<>(word_frequency.entrySet()); // entrySet возвращает Set всех элементов Map (пар)
        sorted_words.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

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
            System.out.println("Ошибка при записи в CSV файл: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Ошибка: укажите имя входного файла как единственный аргумент.");
            return;
        }

        String input_file = args[0];
        WordFrequencyAnalyzer analyzer = new WordFrequencyAnalyzer();

        try {
            Map<String, Integer> word_frequency = analyzer.analyzeFile(input_file);

            // создание выходного CSV файла
            String output_file = "src\\task1\\output.csv";
            analyzer.writeToCSV(word_frequency, output_file);
            System.out.println("Создан CSV файл: " + output_file);
        } catch (IOException e) {
            System.out.println("Ошибка при обработке файла " + input_file + ": " + e.getMessage());
        }
    }
}
