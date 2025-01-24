# Отчет по 2 заданию "Стековый калькулятор"

## 1.1 ExecutionContest.java
```java
public class ExecutionContext {
    private final Stack<Double> stack = new Stack<>();
    private final Map<String, Double> variables = new HashMap<>();

    public Stack<Double> GetStack() {
        return stack;
    }

    public Map<String, Double> GetVariables() {
        return variables;
    }
}
```
Класс в котором храненятся данные, 
необходимые для выполнения команд.
  
В качестве аттрибутов класса здесь две переменные с 
спецификатором доступа `private`:  
1. `private final Stack<Double> stack = new Stack<>();` 
для хранения чисел с которыми будут проводиться операции.  
2. `private final Map<String, Double> variables = new HashMap<>();`
это ассоциативный массив для хранения переменных и их значений. 
Значения из массива будут перемещаться в стек.

## 1.2 Command.java
```java
public abstract class Command {
    public abstract void Execute(ExecutionContext context, String[] args) throws Exception;
}
```
Это абстрактный класс, использующийся как основа для других (дочерних) классов.
Так как класс абстрактный, о чем свидетельствует
ключевое слово `abstract` у класса и его метода, то объект класса
`Command` не может быть создан напрямую.

Класс обеспечивает единообразие реализации команд (дочерних классов), 
также проще расширять функционал отдельных команд по необходимости.

Абстрактный метод  
`public abstract void Execute(ExecutionContext context, String[] args) throws Exception;`  
принимает текущий контекст исполнения и аргументы для комманды (1 или 2).
  
## 1.3 CommandFactory.java
```java
class CommandFactory {
    private static final Map<String, String> command_map = new HashMap<>();

    static {
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
```
  
Данный класс создает объекты команд на основе их имени, 
предварительно загружая файл конфигурации `command.properties`
  
### Рассмотрим сначала статический блок вне методов:  
1.```InputStream input = CommandFactory.class.getResourceAsStream("/commands.properties")```  

`CommandFactory.class` возвращает объект `Class`, представляющий класс `CommandFactory`. 
Далее с помощью [getResourceAsStream](https://docs.oracle.com/javase/8/docs/api/java/lang/ClassLoader.html#getResourceAsStream-java.lang.String-) 
получаем ресурс в виде потока ввода (`InputStream`)
> [!NOTE]  
> Ресурс — это файл, доступный внутри вашей программы (обычно (как и у меня) в папке resources)

> [!NOTE]  
> Путь, начинающийся с /, указывает, что файл ищется от корня
2. Загрузка пар в объект типа Properties:
```java
Properties properties = new Properties();
properties.load(input);
``` 
Создаём объект класса [Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html) для работы с парами "ключ-значение", 
и затем загружаем все пары (key-value) из файла `/commands.properties` с помощью метода
[load](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.InputStream-).

3. Запонение `command_map` парами
```java
for (String name : properties.stringPropertyNames()) {
    command_map.put(name, properties.getProperty(name));
}
```
Итерация по всем ключам с помощью метода [stringPropertyNames](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#stringPropertyNames--),
а метод `properties.getProperty(name)` возвращает значение, связанное с переданным ключом `name`.
  
### Теперь вкратце рассмотрим метод `CreateCommand`
```java
public static Command CreateCommand(String name) throws Exception {
        String class_name = command_map.get(name);
        if (class_name == null) {
            throw new IllegalArgumentException("Неизвестная команда: " + name);
        }
        return (Command) Class.forName(class_name).getDeclaredConstructor().newInstance();
    }
```  
Это впринципе метод ради которого и создается фабрика. Мы из уже 
построенной благодаря статичному методу карты `command_map`. 
Мы берем имя класса команды (например: `name = "PUSH", class_name = "task2.commands.PushCommand"`).
  
И теперь самая сложная для понимания строка:
```java
return (Command) Class.forName(class_name).getDeclaredConstructor().newInstance();
```
Здесь метод [forName(class_name)](https://www.geeksforgeeks.org/class-forname-method-in-java-with-examples/)
загружает класс с именем `class_name`, затем метод [getDeclaredConstructor()](https://www.geeksforgeeks.org/class-getdeclaredconstructor-method-in-java-with-examples/)
вызывает конструктор без параметров для только созданного класса, а [newInstance()](https://www.javatpoint.com/new-instance()-method) создаёт объект класса.
Ну и выполняется приведение созданного объекта к родительскому типу `Command`.

## 1.4 Дочерние команды класса `Command`
Данные классы однотипные и я рассмотрю только один пример: класс `DefineCommand`:
```java
public class DefineCommand extends Command {
    @Override
    public void Execute(ExecutionContext context, String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Команда DEFINE принимает два аргумента!");
        }

        String variable = args[0];
        double value = Double.parseDouble(args[1]);
        context.GetVariables().put(variable, value);
    }
}
```  
Во-первых ключевое слово `extends` говорит нам о наследовании всех аттрибутов 
и методов родительского класса с возможностью добавления 
новых, которые будут только у дочернего класса.

Мы проверяем что получено ровно 2 параметра, затем 
извлекаем имя переменной из первого аргумента (`args[0]`) и ее значение
(`args[1]`). Затем сохраняем переменную и её значение в контекст выполнения
с помощью метода [put(variable, value)](https://www.geeksforgeeks.org/hashmap-put-method-in-java/).

## 1.5 Main.java
```java
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        ExecutionContext context = new ExecutionContext();
        
        try (BufferedReader reader = args.length > 0
                ? new BufferedReader(new FileReader(args[0]))
                : new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); 
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(" ");
                String command_name = parts[0].toUpperCase(); 
                String[] command_args = Arrays.copyOfRange(parts, 1, parts.length); 

                logger.log(Level.INFO, "Команда получена: " + command_name + " " + Arrays.toString(command_args));
                try {
                    Command command = CommandFactory.CreateCommand(command_name);
                    command.Execute(context, command_args);
                    logger.log(Level.INFO, "Команда выполнена: " + command_name + " " + Arrays.toString(command_args));
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Ошибка при выполнении команды: " + command_name, e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при чтении входного файла", e);
        }
    }
}
```
Сначала поговорим про журналирование (logging):
Если почитать про сам класс [Logger](https://docs.oracle.com/javase/8/docs/api/java/util/logging/Logger.html):
1. A Logger object is used to log messages for a specific system or application component. 
2. Logging messages will be forwarded to registered Handler objects, which can forward the messages to a variety of destinations, including <ins>consoles</ins>, files, OS logs, etc.
3. Each Logger has a "Level" associated with it. This reflects a minimum Level that this logger cares about. If a Logger's level is set to null, then its effective level is inherited from its parent, which may in turn obtain it recursively from its parent, and so on up the tree.

Более подробно с уровнями каналирования можно ознакомиться [здесь](https://www.papertrail.com/solution/tips/logging-in-java-best-practices-and-tips/)
(смотреть именно **Java Logging Levels**)

Мы создаем логгер с именем класса `Main` (`Main.class.getName()`) с 
помощью метода [getLogger(String name)](https://docs.oracle.com/javase/8/docs/api/java/util/logging/Logger.html#getLogger-java.lang.String-).  
Логгирование информации происходи с помощью метода [public void log(Level level, String msg)](https://docs.oracle.com/javase/8/docs/api/java/util/logging/Logger.html#log-java.util.logging.Level-java.lang.String-):
```java
logger.log(Level.INFO, "Команда получена: " + command_name + " " + Arrays.toString(command_args));
```

Логи используются для наблюдения за состоянием программы в реальном времени, 
также понятно что происходило перед возможной ошибкой.

## 2.1 Тесты, покрывающие функционал калькулятора
Туториал по тестам, который я смотрел [здесь](https://junit.org/junit5/docs/current/user-guide/#writing-tests)  

Ниже пример моего теста по туториалу:
```java
import org.junit.jupiter.api.Test;

import task2.ExecutionContext;
import task2.commands.AddCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddCommandTest {
    @Test
    void testAdd() {
        ExecutionContext context = new ExecutionContext();
        AddCommand add_command = new AddCommand();

        context.GetStack().push(5.0);
        context.GetStack().push(3.0);

        add_command.Execute(context, new String[]{});

        assertEquals(1, context.GetStack().size());
        assertEquals(8.0, context.GetStack().peek());
    }

    @Test
    void testAddInsufficientOperands() {
        ExecutionContext context = new ExecutionContext();
        AddCommand add_command = new AddCommand();

        context.GetStack().push(5.0);

        assertThrows(IllegalArgumentException.class, () -> add_command.Execute(context, new String[]{}));
    }
}
```
Единственная используемая аннотация: `@Test` - Denotes that a method is a test method. 
Используемое исключение: `public static <T extends Throwable> T assertThrows(Class<T> expectedType,
Executable executable)` - Assert that execution of the supplied executable throws an exception of the expectedType and return the exception.  
  
Сам пример исключения писался по аналогии с примером из [туториала](https://junit.org/junit5/docs/current/user-guide/#writing-tests-exceptions-expected)