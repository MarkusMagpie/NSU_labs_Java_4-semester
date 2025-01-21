package task1;

public class Main {
    public static void main(String[] args) {
        // передаём имя файла в качестве аргумента
        String fileName = "src\\task1\\input.txt";
//        String test = "abc";

        // создаём экземпляр WordFrequencyAnalyzer и вызываем его метод
        WordFrequencyAnalyzer analyzer = new WordFrequencyAnalyzer();
        analyzer.Analyze(new String[]{fileName});
    }
}
