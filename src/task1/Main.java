package task1;

public class Main {
    public static void main(String[] args) {
        // передаём имя файла в качестве аргумента
        String fileName = "src\\task1\\input.txt";
//        String test = "abc";

        // создаём экземпляр WordFrequencyAnalyzer и вызываем его метод
        WordFrequencyAnalyzer.analyze(new String[]{fileName}); // всегда 1 входной параметр так что смысла в 1 проверке нет
    }
}
