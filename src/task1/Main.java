package task1;

public class Main {
    public static void main(String[] args) {
        // передаём имя файла в качестве аргумента
        String file = "src\\task1\\input.txt";

//        WordFrequencyAnalyzer analyzer = new WordFrequencyAnalyzer();
        WordFrequencyAnalyzer.main(new String[]{file});
    }
}
