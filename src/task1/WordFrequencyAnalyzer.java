package task1;

import java.io.*;
import java.util.*;

public class WordFrequencyAnalyzer {
    public static void main(String[] args) {
        // проверка что на входе 1 аргумент
        if (args.length != 1) {
            System.out.println("Ошибка: неверное количество аргументов.");
            return;
        }

        String input_file = args[0];
        Map<String, Integer> word_frequency = new HashMap<>();
        int word_count = 0;

        // открываем входной файл и читаем его
        try (Reader reader = new InputStreamReader(new FileInputStream(input_file))) {
            StringBuilder sb = new StringBuilder();
            int current_char;

            // read symbols from input file once at a time
            while ((current_char = reader.read()) != -1) {
                char c = (char) current_char;

                if (Character.isLetterOrDigit(c)) {
                    sb.append(Character.toLowerCase(c));
                } else if (!sb.isEmpty()) {
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
        } catch (IOException e) {
            System.out.println("Ошибка при чтении из файла: " + e.getMessage());
            return;
        }

        // создаем список из Map.Entry<String, Integer> и сортируем его в порядке убывания
        List<Map.Entry<String, Integer>> sorted_words = new ArrayList<>(word_frequency.entrySet());
        sorted_words.sort((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()));

        // создаем выходной CSV файл
        String output_file = "src\\task1\\output.csv";
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

        System.out.println("Создан CSV файл: " + output_file);
    }
}
