# Отчет по 1 заданию "CSV парсер"

## 1 WordFrequencyAnalyzer.java
Сначала разберу атрибуты класса `public class WordFrequencyAnalyzer`, затем 3 его функции:  
### 1.1 Class attributes 
```java
private static final Logger logger = LogManager.getLogger(Main.class);

private final Map<String, Integer> word_frequency = new HashMap<>();
private int word_count;
```
 - `Logger logger` - используется для записи информации и ошибок в процессе работы программы.
 Интегрирован через библиотеку `Log4j`. Для понимания использования смотри [сюда](https://howtodoinjava.com/log4j2/maven-gradle-config/).  
 - `HashMap<String, Integer> word_frequency` - карта, где ключ - слово, а значение - количество его вхождений.  
 - `int word_count` - счетчик общего числа слов в файле.

### 1.2 Метод `analyzeFile`
```java
public Map<String, Integer> analyzeFile(String filename) throws IOException {
    Reader reader = new InputStreamReader(new FileInputStream(filename));
    StringBuilder sb = new StringBuilder();
    int current_char;

    while ((current_char = reader.read()) != -1) {
        char c = (char) current_char;

        if (Character.isLetterOrDigit(c)) {
            sb.append(Character.toLowerCase(c));
        } else if (!sb.isEmpty()) {
            String word = sb.toString().toLowerCase();
            word_frequency.put(word, word_frequency.getOrDefault(word, 0) + 1);
            word_count++;
            sb.setLength(0);

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
```
Этот метод анализирует входящий текстовый файл и подсчитывает частоты слов в нем.  
Очевидно что слова читаем посимвольно методом [read()](https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html#read--). 
Символы составляют слова, 
которые добавляются методом [append(char c)](https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html#append-char-) 
в объект `StringBuilder sb`. Учитываем что по условию символ это
цифра или буква (проверяется методом [Character.isLetterOrDigit(char ch)](https://www.javatpoint.com/java-character-isletterordigit-method)), 
значит если встретили не такой символ то мы дошли до
сепаратора а значит надо добавлять слово в `word_frequency` и обнулять `sb`.  

> На всякий пожарный оговори что при инициализации объекта reader мы
> используем FileInputStream, который есть подкласс класса [InputStream](https://docs.oracle.com/javase/8/docs/api/java/io/InputStream.html),
> а класс [InputStreamReader](https://docs.oracle.com/javase/8/docs/api/java/io/InputStreamReader.html) 
> есть подкласс класса Reader.

Ну и по завершению посимвольного прохода входного файла логгер
выводит информацию об общем количестве слов и возвращаем построенную 
`HashMap<String, Integer> word_frequency`.  

### 1.3 Метод `writeToCSV`
```java
private void writeToCSV(Map<String, Integer> word_frequency, String output_file) {
    List<Map.Entry<String, Integer>> sorted_words = new ArrayList<>(word_frequency.entrySet()); // entrySet возвращает Set всех элементов Map (пар)
    sorted_words.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

    try (PrintWriter writer = new PrintWriter(new FileOutputStream(output_file))) {
        writer.println("Слово,Частота,Частота (%)");

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
```
Записывает данные о словах и их частотах в CSV-файл.  
Здесь представляет интерес только применение лямбда-сортировки списка `sorted_words`:
```java
List<Map.Entry<String, Integer>> sorted_words = new ArrayList<>(word_frequency.entrySet()); // entrySet возвращает Set всех элементов Map (пар)
sorted_words.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
```
Объект `sorted_words` — это переменная типа интерфейса `List<Map.Entry<String, Integer>>`, 
но для создания объекта используем конкретную его реализацию `ArrayList`. 
Полиморфизм: переменная `sorted_words` объявлена как
переменная типа `List` но ссылается на объект класса который реализует интерфейс.  

Теперь само лямбда выражение:
- `e1` и `e2` — это два элемента типа `Map.Entry<String, Integer>`. 
Каждый элемент содержит пару K-V. Например слово "привет" встретилось 2 раза. "привет"-ключ, 2-значение.
- [getValue()](https://docs.oracle.com/javase/8/docs/api/java/util/Map.Entry.html#getValue--) 
извлекает значение из `Map.Entry` (извлек значение 2 от "привет")
- `e2.getValue().compareTo(e1.getValue())` - вызов метода `compareTo()` класса `Integer`,
  который сравнивает два целых числа: `e2.getValue()` и `e1.getValue()`.
  Значит, элементы с большим значением будут идти перед элементами с меньшими значениями, 
что нам и надо по условию.
  
Пример с которого я писал лямбда-выражение [здесь](https://www.javatpoint.com/java-list-sort-lambda).

[Здесь](https://www.w3schools.com/java/java_advanced_sorting.asp) хорошо написано про интерфейс `Comparator`.

## 2 Main.java

### Метод `main(String[] args)` 
```java
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

            String output_file = "task1/src/main/resources/output.csv";
            analyzer.writeToCSV(word_frequency, output_file);

            logger.info("CSV-файл создан: " + output_file);
        } catch (IOException e) {
            System.out.println("Error while reading file: " + input_file + ": " + e.getMessage());
        }
    }
}
```
Это основной метод класса. Проверяем, что передан ровно один аргумент
(имя входного файла). Если передано несколько аргументов, то журналируем этот факт.
Также журналируем факт создания CSV файла.