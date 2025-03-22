# Отчет по 4 заданию "Многопоточное программирование.Эмулятор работы фабрики"

Отчет разделен на две части:
1. Техническая составляющая работы фабрики, логика работы классов.
2. Графический интерфейс, где можно смотреть основные параметры работы фабрики и
   контролировать процесс.

## 1.1 Класс `Part` и наследуемые от него `Body`, `Motor`, `Accessory`

Родительский класс (еще называют `superclass`) `Part`.
Это базовый класс, представляющий собой деталь. Содержит общую логику
для всех трех типов деталей.
```java
public class Part {
    private final int id;

    public Part(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
```
Ниже классы наследуют от класса `Part` аттрибуты и методы. Все три класса
будут выполнять родительский конструктор, передавая ему параметр `int id`.
```java
public class Body extends Part {
    public Body(int id) {
        super(id);
    }
}
```

```java
public class Motor extends Part {
    public Motor(int id) {
        super(id);
    }
}
```

```java
public class Accessory extends Part {
    public Accessory(int id) {
        super(id);
    }
}
```
Так как они все наследуют методы родительского класса то можем получать
идентификатор каждой детали, невзирая на ее тип. Например:
```java
Body body = new Body(1);
Motor motor = new Motor(2);
Accessory accessory = new Accessory(3);

System.out.println("Body ID: " + body.getId()); // вывод: Body ID: 1
System.out.println("Motor ID: " + motor.getId()); // вывод: Motor ID: 2
System.out.println("Accessory ID: " + accessory.getId()); // вывод: Accessory ID: 3
```

## 1.2 Класс `Storage<T extends Part>`
```java
public class Storage<T extends Part> {
    private final Queue<T> items = new LinkedList<>();
    private final int capacity;

    public Storage(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void add(T item) throws InterruptedException {
        while (items.size() >= capacity) {
            System.out.println("Storage is full for " + item.getClass().getSimpleName());
            wait();
        }

        items.add(item);
        System.out.println("Added to Storage: " + item + " with id " + item.getId() + " by " + Thread.currentThread().getName());
        notifyAll();
    }

    public synchronized T get() throws InterruptedException {
        while (items.isEmpty()) {
            wait();
        }

        T item = items.poll();
        System.out.println("Got from Storage: " + item + " with id " + item.getId() + " by " + Thread.currentThread().getName());
        notifyAll();

        return item;
    }

    public int getCurrentSize() {
        return items.size();
    }
}
```
Класс `Storage` - хранилище для деталей типа `T`, где `T` - это любой
класс, наследующийся от `Part` (`Body`, `Motor` или `Accessory`). 

Хранилище имеет ограниченную вместимость `capacity` и реализует 
потокобезопасные методы для добавления и извлечения деталей из него.

Рассмотрим методы:
### 1.2.1 метод `add(T item)`
```java
public synchronized void add(T item) throws InterruptedException {
    while (items.size() >= capacity) {
        System.out.println("Storage is full for " + item.getClass().getSimpleName());
        wait();
    }

    items.add(item);
    System.out.println("Added to Storage: " + item + " with id " + item.getId() + " by " + Thread.currentThread().getName());
    notifyAll();
}
```
Если хранилище заполнено, то поток блокируется и ждет, 
пока не освободится место. После добавления детали, поток уведомляет 
об этом все ожидающие потоки.  
Метод синхронизирован - только один поток может выполнять его одновременно.  

Метод [wait()](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#wait--) - 
переводит вызывающий поток в состояние ожидания до тех пор, 
пока другой поток не вызовет метод `notify()`, также метод отпускает 
монитор объекта (т.е. разрешает другим потокам работать с этим объектом).

Метод [notifyAll()](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#notifyAll--) -
возобновляет работу всех потоков, у которых ранее был вызван метод `wait()`.

### 1.2.2 метод `get()`
```java
public synchronized T get() throws InterruptedException {
    while (items.isEmpty()) {
        wait();
    }

    T item = items.poll();
    System.out.println("Got from Storage: " + item + " with id " + item.getId() + " by " + Thread.currentThread().getName());
    notifyAll();

    return item;
}
```
Синхронизированный метод извлекает деталь из хранилища `items`.  
Если хранилище пусто, то поток ждет, пока не появится новая деталь.  
Для извлечения используем метод [poll()](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html#poll--).

Напоминаю что [LinkedList](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html) 
реализует интерфейс `Queue` а значит мы можем использовать метод `poll()`.

> Использование `wait()` и `notifyAll()` позволяет потокам корректно
> взаимодействовать: потоки ждут, когда хранилище освободится 
> или заполнится, и уведомляют друг друга об изменениях.

> ВАЖНО! В обоих методах используется цикл `while` для проверки условий.
> Почему не просто `if`?
> 
> Представь:
> 1. Два покупателя ждут товара `get()`  
> 2. Рабочий добавляет товар и вызывает `notifyAll()`  
> 3. Оба покупателя просыпаются, но первый забирает товар (покупатели
> забирают товары синхронизированно), а второй видит, 
> что полка уже пуста. Без `while` второй покупатель упал бы с ошибкой, 
> так как `items.poll()` вернул бы `null`!
> 
> `while` гарантирует, что после пробуждения поток снова проверит 
> условие (полка пуста?), прежде чем продолжить работу.

## 1.3 Класс `Supplier` - его экземпляры выполняются потоками
```java
public class Supplier<T extends Part> implements Runnable {
    private final Storage<T> storage;
    private volatile int delay;
    private final Class<T> part_type;
    private static int id_counter = 0;

    public Supplier(Storage<T> storage, int delay, Class<T> part_type) {
        this.storage = storage;
        this.delay = delay;
        this.part_type = part_type;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                T part = createPart();
                storage.add(part);
                Thread.sleep(delay);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public T createPart() {
        try {
            return part_type.getConstructor(int.class).newInstance(++id_counter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create part " + part_type + " with id " + id_counter, e);
        }
    }
}
```
Класс отвечает за создание деталей типа `T` (`Body`, `Motor` и `Accessory`) 
и их добавление в хранилище. 
Он реализует функциональный интерфейс `Runnable`, 
что позволяет ему работать в отдельном потоке.

Поля класса:
- `private final Storage<T> storage` - хранилище, в которое будут добавляться созданные детали.
- ` private volatile int delay` - задержка (в миллисекундах) между созданием и добавлением каждой детали. 
Ключевое слово `volatile` означает что любые изменения, 
внесенные в переменную в одном потоке, будут немедленно видны всем 
другим.
- `private final Class<T> part_type` - это класс типа `T`, который будет создаваться.
- `private static int id_couner = 0` - счетчик уникальных идентификаторов деталей.

### 1.3.1 перегруженный метод `run()`
```java
@Override
public void run() {
    try {
        while (!Thread.currentThread().isInterrupted()) {
            T part = createPart();
            storage.add(part);
            Thread.sleep(delay);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}
```
В бесконечном цыкле вплоть до прерывания потока создаем новую деталь, добавляем ее в хранилище и
ждем указанное время. Если поток прерван, то завершаем работу.

### 1.3.2 метод `createPart()`
```java
public T createPart() {
    try {
        return part_type.getConstructor(int.class).newInstance(++id_counter);
    } catch (Exception e) {
        throw new RuntimeException("Failed to create part " + part_type + " with id " + id_counter, e);
    }
}
```
Здесь создается новая деталь с уникальным идентификатором.  
Рассмотрим подробнее строку:
```java
part_type.getConstructor(int.class).newInstance(++id_counter)
```
Здесь у `Class<T> part_type` вызывается конструктор, который принимает один
аргумент типа `int`. После получения конструктора класса `newInstance(...)`
вызывает этот конструктор и создаёт новый объект класса `part_type`.

> Из документации метода [newInstance(...)](https://www.javatpoint.com/new-instance()-method) 
> вычитаем:  
> Uses the constructor represented by this Constructor object to create and initialize a new instance of the constructor's declaring class, with the specified initialization parameters.


## 1.4 Класс `Car`
```java
public class Car extends Part{
    private final Body body;
    private final Motor motor;
    private final Accessory accessory;

    public Car(int id, Body body, Motor motor, Accessory accessory) {
        super(id);
        this.body = body;
        this.motor = motor;
        this.accessory = accessory;
    }

    public Body getBody() {
        return body;
    }

    public Motor getMotor() {
        return motor;
    }

    public Accessory getAccessory() {
        return accessory;
    }
}
``` 
Класс `Car` представляет собой автомобиль, собирается из трех 
компонентов: `Body`, `Motor` и `Accessory`. 
Он наследуется от класса `Part`, что позволяет ему иметь 
уникальный идентификатор, как и все его детали.

`extends Part` - автомобиль считается деталью, что логично, если, 
он является конечным продуктом, который тоже может храниться и учитываться.

## 1.5 Класс `Worker` - его экземпляры выполняются потоками
```java
public class Worker implements Runnable {
    private final Storage<Body> body_storage;
    private final Storage<Motor> motor_storage;
    private final Storage<Accessory> accessory_storage;
    private final Storage<Car> car_storage;
    private static int car_counter = 0;

    public Worker(Storage<Body> body_storage, Storage<Motor> motor_storage, Storage<Accessory> accessory_storage, Storage<Car> car_storage) {
        this.car_storage = car_storage;
        this.body_storage = body_storage;
        this.motor_storage = motor_storage;
        this.accessory_storage = accessory_storage;
    }

   @Override
   public void run() {
      try {
         Body body = body_storage.get();
         Motor motor = motor_storage.get();
         Accessory accessory = accessory_storage.get();

         Car car = new Car(++car_counter, body, motor, accessory);
         car_storage.add(car);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }
}
```
`22.03` - в данном классе происходит сборка одного автомобиля из его частей, и поставка
автомобиля на хранилище готовой продукции.

### 1.5.1 метод `run()`
```java
@Override
public void run() {
   try {
      Body body = body_storage.get();
      Motor motor = motor_storage.get();
      Accessory accessory = accessory_storage.get();

      Car car = new Car(++car_counter, body, motor, accessory);
      car_storage.add(car);
   } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
   }
}
```
`22.03` - Рабочий теперь выполняет сборку одного автомобиля как 
задачу, после чего завершается. 
Это сделано для того, чтобы контроллер склада мог создавать задачи
на сборку автомобиля только при появлении свободного места на складе 
готовой продукции. 
Таким образом, рабочие не работают в бесконечном цикле, а получают задачу, 
выполняют сборку и завершают свою работу.  

Методы `body_storage.get()`, `motor_storage.get()`, 
`accessory_storage.get()` блокируют поток, если соответствующих 
деталей нет в хранилищах. Рабочий ждет, пока детали не появятся и только
затем строит машину и пробует ее добавить в хранилище готовой продукции:
если хранилище заполнено, поток блокируется до освобождения места.

Поток может не блокироваться, а просто быть прерванным по обработке исключений.

Важные замечания:
1. `22.03` - `Controller` склада готовой продукции отслеживает количество
автомобилей в хранилище и, когда обнаруживает освобождение места 
(после продажи автомобиля), 
добавляет новую задачу в пул потоков для сборки автомобиля.

2. Использование `synchronized` методов в классе `Storage` 
гарантирует корректную работу с хранилищами в многопоточной среде.
3. Если в хранилищах нет деталей или место в `car_storage` заполнено, 
поток рабочего блокируется до изменения условий.

## 1.6 Класс `Dealer` - его экземпляры выполняются потоками
```java
public class Dealer implements Runnable {
    private static final Logger logger = LogManager.getLogger(Dealer.class);
    
    private final Storage<Car> car_storage;
    private volatile int delay;
    private final boolean log_enabled;
    private int sold_cars = 0;

    public Dealer(Storage<Car> car_storage, int delay, boolean log_enabled) {
        this.car_storage = car_storage;
        this.delay = delay;
        this.log_enabled = log_enabled;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Car car = car_storage.get();
                ++sold_cars;
                System.out.println("Sold: " + car.toString() + " with id " + car.getId());
                if (log_enabled) { writeLog(car); }
                Thread.sleep(delay);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void writeLog(Car car) {
        String message = String.format("%d: Dealer: Car %d (Body: %d, Motor: %d, Accessory: %d)",
                System.currentTimeMillis(), car.getId(),
                car.getBody().getId(), car.getMotor().getId(),
                car.getAccessory().getId());
        logger.info(message);
    }

    public int getSoldCars() {
        return sold_cars;
    }
}
```
Задача дилера - забирать автомобили со склада готовой продукции и "продавать".

По условию: при отправке машины дилеру информация о покупке должна 
писаться в лог работы фабрики (в файл) в виде строки:  
&lt;Time&gt;: Dealer &lt;Number&gt;: Auto &lt;ID&gt; (Body: &lt;ID&gt;, Motor: &lt;ID&gt;, Accessory: &lt;ID&gt;)  
Поэтому в полем класса является объект `logger` – логгер из Log4j.

### 1.6.1 Метод `run()`
```java
@Override
public void run() {
    try {
        while (!Thread.currentThread().isInterrupted()) {
            Car car = car_storage.get();
            ++sold_cars;
            System.out.println("Sold: " + car.toString() + " with id " + car.getId());
            if (log_enabled) { writeLog(car); }
            Thread.sleep(delay);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}
```
Опять работаем в бесконечном цикле, пока поток не будет прерван. Забираем 
машину из хранилища и если включено `log_enabled`, то пишем в файл 
`factorylog.txt` лог в виде вышеописанной строки с помощью метода `writeLog(Car car)`.  
Затем дилер ждет некоторое заданное время и возобновляет процесс "продажи".

### 1.6.2 Метод `writeLog(Car car)`
```java
private void writeLog(Car car) {
    String message = String.format("%d: Dealer: Car %d (Body: %d, Motor: %d, Accessory: %d)",
            System.currentTimeMillis(), car.getId(),
            car.getBody().getId(), car.getMotor().getId(),
            car.getAccessory().getId());

    logger.info(message);
}
```
Здесь резонно сказать о том как вообще логи записываются в нужный файл:  
в следующей строке:  
```java
private static final Logger logger = LogManager.getLogger(Dealer.class);
```
Log4j2 автоматически ищет конфигурационный файл и настраивает логирование.
Рассмотрим этот файл в директории `resources`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <File name="File" fileName="task4/src/main/resources/factorylog.txt">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="File"/>
        </Root>
    </Loggers>
</Configuration>
```
 - `Appender` – это место, куда Log4j2 записывает логи. То есть 
определен файловый логгер, который пишет в файл `factorylog.txt`.
 - Определение логгеров `<Loggers>`:
```xml
<Loggers>
    <Root level="info">
        <AppenderRef ref="File"/>
    </Root>
</Loggers>
```
`<AppenderRef ref="File"/>` – указывает, что все логи корневого логгера
должны записываться в "File" (наш файл `factorylog.txt`).

Итак. В конфигурационном файле описан путь к файлу и определен файловый 
логгер который и пишет нужную информацию о фабричной продукции к нам в
файл.

Как происходит запись в программе?
1. `LogManager.getLogger(Dealer.class)` создаёт логгер, 
привязанный к классу `Dealer`.
2. `logger.info(message)`   
   Log4j2:  
    - Проверяет уровень (`INFO`)  
    - Если `INFO` разрешён (в конфиге видим level="info", значит да), 
то отправляет сообщение в `Appender` "File".  
    - Файл `factorylog.txt` получает запись с форматом, заданным 
в `<PatternLayout>`.

## 1.7 Класс `ConfigLoader`
```java
class ConfigLoader {
    private final Properties properties = new Properties();

    public ConfigLoader(String config_file_path) {
        try (FileInputStream input = new FileInputStream(config_file_path)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file " + config_file_path, e);
        }
    }

    public int getStorageBodySize() {
        return Integer.parseInt(properties.getProperty("StorageBodySize", "100"));
    }
    
    // ... дальше однотипные геттер-методы
}
```

### 1.7.1 Конструктор `ConfigLoader(String config_file_path)`
```java
public ConfigLoader(String config_file_path) {
    try (FileInputStream input = new FileInputStream(config_file_path)) {
        properties.load(input);
    } catch (IOException e) {
        throw new RuntimeException("Failed to load config file " + config_file_path, e);
    }
}
```
Сам переданный аргументом файл читаем с помощью конструктора [FileInputStream(File file)](https://docs.oracle.com/javase/8/docs/api/java/io/FileInputStream.html#FileInputStream-java.io.File-).  
Загружаем в поле `properties` считанный файл с помощью метода [load(InputStream inStream)](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.InputStream-):
> Reads a property list (key and element pairs) from the input byte stream. 

### 1.7.2 Геттер-метод `getStorageBodySize()`
```java
public int getStorageBodySize() {
    return Integer.parseInt(properties.getProperty("StorageBodySize", "100"));
}
```
Здесь используя метод [getProperty(String key, String defaultValue)](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#getProperty-java.lang.String-java.lang.String-)
ищем `value` с указанным ключом `key` в текущем списке свойств `Properties`.
Затем конвертируем полученный `value` в `int` через `Integer.parseInt()`.

## 1.8 Класс `Main` - точка входа в программу
```java
public class Main {
    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader("task4\\src\\main\\resources\\config.properties");

        List<Supplier<Body>> body_suppliers = new ArrayList<>();
        List<Supplier<Motor>> motor_suppliers = new ArrayList<>();
        List<Supplier<Accessory>> accessory_suppliers = new ArrayList<>();
        List<Dealer> dealers = new ArrayList<>();
        
        Storage<Body> bodyStorage = new Storage<>(configLoader.getStorageBodySize());
        Storage<Motor> motorStorage = new Storage<>(configLoader.getStorageMotorSize());
        Storage<Accessory> accessoryStorage = new Storage<>(configLoader.getStorageAccessorySize());
        Storage<Car> carStorage = new Storage<>(configLoader.getStorageCarSize());

        int body_supplier_delay = 4000;
        for (int i = 0; i < configLoader.getBodySuppliers(); ++i) {
            Supplier<Body> supplier = new Supplier<>(bodyStorage, body_supplier_delay, Body.class);
            Thread thread = new Thread(supplier);
            body_suppliers.add(supplier);
            thread.start();
        }
        
        int motor_supplier_delay = 4000;
        for (int i = 0; i < configLoader.getMotorSuppliers(); ++i) {
            Supplier<Motor> supplier = new Supplier<>(motorStorage, motor_supplier_delay, Motor.class);
            Thread thread = new Thread(supplier);
            motor_suppliers.add(supplier);
            thread.start();
        }
        
        int accessory_supplier_delay = 4000;
        for (int i = 0; i < configLoader.getAccessorySuppliers(); ++i) {
            Supplier<Accessory> supplier = new Supplier<>(accessoryStorage, accessory_supplier_delay, Accessory.class);
            Thread thread = new Thread(supplier);
            accessory_suppliers.add(supplier);
            thread.start();
        }

        // пул потоков ONLY для рабочих
        ThreadPool threadPool = new ThreadPool(configLoader.getWorkers());

       Controller controller = new Controller(carStorage, threadPool, configLoader.getStorageCarSize(),
               bodyStorage, motorStorage, accessoryStorage);
       new Thread(controller).start();

        try (FileWriter writer = new FileWriter("task4\\src\\main\\resources\\factorylog.txt", false)) {
        } catch (Exception e) {
            System.out.println("Failed to clear log file: " + e.getMessage());
        }
        
        int dealer_delay = 5000;
        for (int i = 0; i < configLoader.getDealers(); ++i) {
            Dealer dealer = new Dealer(carStorage, dealer_delay, configLoader.isLogEnabled());
            Thread thread = new Thread(dealer);
            dealers.add(dealer);
            thread.start();
        }

        FactoryGUI gui = new FactoryGUI(body_suppliers, motor_suppliers, accessory_suppliers, dealers,
                configLoader.getStorageBodySize(), configLoader.getStorageMotorSize(),
                configLoader.getStorageAccessorySize(), configLoader.getStorageCarSize(),
                body_supplier_delay, motor_supplier_delay, accessory_supplier_delay, dealer_delay);
        gui.setVisible(true);

        Timer timer = new Timer(1000, e -> {
            int total_sold_cars = 0;
            for (Dealer dealer : dealers) {
                total_sold_cars += dealer.getSoldCars();
            }
            gui.updateStats(
                    bodyStorage.getCurrentSize(),
                    motorStorage.getCurrentSize(),
                    accessoryStorage.getCurrentSize(),
                    carStorage.getCurrentSize(),
                    total_sold_cars
            );
        });
        timer.start();
    }
}
```
Он инициализирует все компоненты (хранилища, поставщиков, рабочих, 
дилеров) и управляет их взаимодействием, 
также предоставляет графический интерфейс для мониторинга системы.

```java
ConfigLoader configLoader = new ConfigLoader("task4\\src\\main\\resources\\config.properties");

List<Supplier<Body>> body_suppliers = new ArrayList<>();
List<Supplier<Motor>> motor_suppliers = new ArrayList<>();
List<Supplier<Accessory>> accessory_suppliers = new ArrayList<>();
List<Dealer> dealers = new ArrayList<>();

Storage<Body> bodyStorage = new Storage<>(configLoader.getStorageBodySize());
Storage<Motor> motorStorage = new Storage<>(configLoader.getStorageMotorSize());
Storage<Accessory> accessoryStorage = new Storage<>(configLoader.getStorageAccessorySize());
Storage<Car> carStorage = new Storage<>(configLoader.getStorageCarSize());
```
Создается объект типа `ConfigLoader` с указанием пути к конфигурационному 
файлу (нужно для удаления информации из него до начала записи логов).  
Затем инициализируются списки для поставщиков деталей, дилеров. 
Создаются хранилища для кузовов, моторов, аксессуаров и автомобилей, 
используя параметры из конфига (размеры хранилищ задаются через конфиг
файл).

### 1.8.1 Создание и запуск потоков поставщиков
```java
int body_supplier_delay = 4000;
for (int i = 0; i < configLoader.getBodySuppliers(); ++i) {
    Supplier<Body> supplier = new Supplier<>(bodyStorage, body_supplier_delay, Body.class);
    Thread thread = new Thread(supplier);
    body_suppliers.add(supplier);
    thread.start();
}
```
Создание и запуск потоков всех трех поставщиков происходит одинаково, 
поэтому рассмотрим только поставщиков кузовов:
создаем потоков столько, сколько сказано в конфиг файле.  
Затем в этой строке запускаем конструктор класса `Supplier`:
```java
Supplier<Body> supplier = new Supplier<>(bodyStorage, body_supplier_delay, Body.class);
```
Добавляем новосозданный объект поставщика в соответствующий `List<Supplier<Body>>`, 
и затем создаем и запускаем поток. Так как в классе `Supplier` 
реализуется интерфейс `Runnable`, то мы вправе это делать.

### 1.8.2 Настройка рабочих
```java
ThreadPool threadPool = new ThreadPool(configLoader.getWorkers());
```
Создаем конструктором класса `ThreadPool` новый пул потоков для 
управления рабочими.

### 1.8.3 Очистка файла лога "factorylog.txt"
```java
try (FileWriter writer = new FileWriter("task4\\src\\main\\resources\\factorylog.txt", false)) {
} catch (Exception e) {
    System.out.println("Failed to clear log file: " + e.getMessage());
}
```
Файл будет очищен, так как `FileWriter` открывает файл в режиме 
перезаписи (второй параметр `false` указывает на это).

### 1.8.4 Создание и запуск потоков дилеров
```java
int dealer_delay = 5000;
for (int i = 0; i < configLoader.getDealers(); ++i) {
    Dealer dealer = new Dealer(carStorage, dealer_delay, configLoader.isLogEnabled());
    Thread thread = new Thread(dealer);
    dealers.add(dealer);
    thread.start();
}
```
создаем потоков столько, сколько сказано в конфиг файле.  
Затем в этой строке запускаем конструктор класса `Dealer`:
```java
Dealer dealer = new Dealer(carStorage, dealer_delay, configLoader.isLogEnabled());
```
Добавляем новосозданный объект поставщика в `List<Dealer>`,
и затем создаем и запускаем поток. Так как в классе `Dealer`
реализуется интерфейс `Runnable`, то мы вправе это делать.

### 1.8.5 Графический интерфейс и таймер обновления в нем статистики
```java
FactoryGUI gui = new FactoryGUI(body_suppliers, motor_suppliers, accessory_suppliers, dealers,
        configLoader.getStorageBodySize(), configLoader.getStorageMotorSize(),
        configLoader.getStorageAccessorySize(), configLoader.getStorageCarSize(),
        body_supplier_delay, motor_supplier_delay, accessory_supplier_delay, dealer_delay);
gui.setVisible(true);

Timer timer = new Timer(1000, e -> {
    int total_sold_cars = 0;
    for (Dealer dealer : dealers) {
        total_sold_cars += dealer.getSoldCars();
    }
    gui.updateStats(
            bodyStorage.getCurrentSize(),
            motorStorage.getCurrentSize(),
            accessoryStorage.getCurrentSize(),
            carStorage.getCurrentSize(),
            total_sold_cars
    );
});
timer.start();
```
Создаем конструктором новый объект который служит графическим интерфейсом 
для отображения:
1. загрузки хранилищ
2. количества проданных автомобилей
3. задержек между операциями
4. ползунки для изменения задержек у потоков поставщиков и дилеров.

Затем используем конструктор класса `Timer` [public Timer(int delay,
ActionListener listener)](https://docs.oracle.com/javase/8/docs/api/javax/swing/Timer.html#Timer-int-java.awt.event.ActionListener-) 
в качестве `ActionListener` даем лямбда-выражение.  
> Стоит оговорить что [ActionListener](https://docs.oracle.com/javase/8/docs/api/?java/awt/event/ActionListener.html) 
есть функциональный интерфейс, а мы знаем, что лямбда-выражение 
не выполняется само по себе, а образует реализацию метода, 
определенного в функциональном интерфейсе!

## `22.03` - 1.9 Класс `Controller` - поддержание количества задач
```java
public class Controller implements Runnable {
    private final ThreadPool threadPool;
    private final int capacity;
    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final Storage<Car> carStorage;

    public Controller(Storage<Car> carStorage, ThreadPool threadPool, int capacity,
                               Storage<Body> bodyStorage, Storage<Motor> motorStorage, Storage<Accessory> accessoryStorage) {
        this.carStorage = carStorage;
        this.threadPool = threadPool;
        this.capacity = capacity;
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
    }

    @Override
    public void run() {
//        System.out.println("Controller started");
        // изначально заполняем пул задач до заполнения склада
        int initialTasks = capacity - carStorage.getCurrentSize();
        for (int i = 0; i < initialTasks; i++) {
            threadPool.addTask(new Task(new Worker(bodyStorage, motorStorage, accessoryStorage, carStorage)));
        }

        // затем контроллер засыпает и ждет, когда будет продана машина (из хранилища)
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (carStorage) {
                try {
                    // ожидаем уведомления от метода get() в хранилище, который вызывается при продаже машины
                    carStorage.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            // при пробуждении вычисляем, сколько свободных мест осталось
            int freeSlots = capacity - carStorage.getCurrentSize();
            for (int i = 0; i < freeSlots; i++) {
                threadPool.addTask(new Task(new Worker(bodyStorage, motorStorage, accessoryStorage, carStorage)));
            }
        }
    }
}
```
Отвечает за поддержание количества задач на сборку автомобилей в 
пуле потоков ровно в количестве, равном вместимости склада 
готовой продукции.   
Логика работы класса `Controller` такова:
1. Заполняем пул задач до заполнения склада:  
```java
int initialTasks = capacity - carStorage.getCurrentSize();
for (int i = 0; i < initialTasks; i++) {
   threadPool.addTask(new Task(new Worker(bodyStorage, motorStorage, accessoryStorage, carStorage)));
}
```
Вычисляем, сколько свободных мест осталось в хранилище автомобилей, 
используя разность между максимальной вместимостью `capacity` и 
текущим количеством автомобилей `carStorage.getCurrentSize()`.  
Затем добавляем в пул потоков ровно столько задач, сколько свободных мест. 
Каждая задача представляет собой одну сборку автомобиля - то есть 
объект класса `Worker`, завершающий работу после сборки одного авто.
2. ожидание события - продажи
```java
synchronized (carStorage) {
    try {
        carStorage.wait();
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
    }
}
```
после  первоначального заполнения задач контроллер 
входит в бесконечный цикл, в котором синхронизируется на объекте
`carStorage` и вызывает метод `wait()`. 
Это значит что контроллер будет засыпать, пока не появится уведомление
`notifyAll()` о том что автомобиль убран из хранилища дилером 
(метод `get()` вызванный дилером). 

3. добавление задач
```java
int freeSlots = capacity - carStorage.getCurrentSize();
for (int i = 0; i < freeSlots; i++) {
    threadPool.addTask(new Task(new Worker(bodyStorage, motorStorage, accessoryStorage, carStorage)));
}
```
когда контроллер опять пробудился, он снова вычисляет, 
сколько свободных мест осталось в хранилище. 
Для каждого освободившегося места контроллер добавляет новую задачу
на сборку автомобиля в пул потоков.

Еще раз вкратце:
- заполнил свободные места
- перешел в режим ожидания с помощью `wait()`
- если дилер забрал машину, а значит вызвал метод `notifyAll()`, контроллер
  пробуждается и пересматривает количество свободных мест


## 1.10 Директория `threadpool`: классы `Task` и `ThreadPool`

### 1.10.1 класс `Task`
```java
public class Task {
    private final Runnable task;

    public Task(Runnable task) {
        this.task = task;
    }

    public void execute() {
        task.run();
    }
}
```
Поле здесь хранит задачу, которую можно выполнить. Я имею ввиду что задача
имеет тип функционального интерфейса [Runnable](https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html).  
Метод `execute()` просто запускает выполнение задачи.  
> `void run()`: When an object implementing interface `Runnable` is used
> to create a thread, starting the thread causes the object's `run` 
> method to be called in that separately executing thread.


### 1.10.2 Класс `ThreadPool`
```java
public class ThreadPool {
    private final Queue<Task> task_queue = new LinkedList<>();
    private final List<Thread> threads;
    private volatile boolean is_running = true;

    public ThreadPool(int num_threads) {
        threads = new ArrayList<>();
        for (int i = 0; i < num_threads; ++i) {
            Thread thread = new Thread(this::workerLoop);
            threads.add(thread);
            thread.start();
        }
    }

    public synchronized void addTask(Task task) {
        if (is_running) {
            task_queue.add(task);
            notify();
        }
    }

    private void workerLoop() {
        while (is_running || !task_queue.isEmpty()) {
            Task task;
            synchronized (this) {
                while (is_running && task_queue.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (!is_running && task_queue.isEmpty()) {
                    return;
                }
                task = task_queue.poll();
            }
            if (task != null) {
                task.execute();
            }
        }
    }

    public void shutdown() {
        is_running = false;
        synchronized (this) {
            notifyAll();
        }
        for (Thread thread : threads) {
            thread.interrupt();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
```
Класс `ThreadPool` реализует пул потоков - механизм, 
который позволяет эффективно управлять выполнением задач
с помощью ограниченного числа потоков. Применяется к рабочим.

Поля:
 - `private final Queue<Task> task_queue = new LinkedList<>()` - это очередь, 
хранящая задачи, которые будут выполняться. 
 - `private final List<Thread> threads` - список рабочих потоков, каждый 
поток будет забирать задачи из `task_queue` методом `poll()` 
и выполнять их методом `execute()` из класса `Task` (подразумевается 
просто `run()`).
 - `private volatile boolean is_running = true` - флаг, управляющий 
работой всех потоков. Ключевое слово `volatile` гарантирует, что 
изменения переменной будут сразу видны всем потокам.

### 1.10.2.1 Конструктор `ThreadPool(int num_threads)`
```java
public ThreadPool(int num_threads) {
    threads = new ArrayList<>();
    for (int i = 0; i < num_threads; ++i) {
        Thread thread = new Thread(this::workerLoop);
        threads.add(thread);
        thread.start();
    }
}
```
Создаем `num_threads` потоков, где каждому передается `this::workerLoop` (это метод, который потоки выполняют),
а затем все созданные потоки добавляются в поле потоков `threads` и запускаются.

### 1.10.2.2 Метод `addTask(Task task)`
```java
public synchronized void addTask(Task task) {
    if (is_running) {
        task_queue.add(task);
        notify();
    }
}
```
Если флаг `true` то добавляем таск в `Queue<Task> task_queue` и 
вызывааем `notify()` чтобы разбудить ожидающий поток.

### 1.10.2.3 Метод `workerLoop()`
```java
private void workerLoop() {
    while (is_running || !task_queue.isEmpty()) {
        Task task;
        synchronized (this) {
            while (is_running && task_queue.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (!is_running && task_queue.isEmpty()) {
                return;
            }
            task = task_queue.poll();
        }
        task.execute();
    }
}
```
Поток работает, пока `is_running == true` **ИЛИ** пока 
есть таски в `task_queue`. (Даже если флаг изменил состояние, 
мы должны завершить выполнение оставшихся в очереди тасков).

Далее синхронизированный блок, объект блокировки - экземпляр `ThreadPool`.
В блоке мы ожидаем таски если пул активен, но очередь тасков пуста.
Если пул остановлен (`is_running == false`) и тасков больше нет, то 
завершаем метод (и поток), так как все таски выполнены.  
Если же и пул активен, и задачи есть, то извлекаем из очереди первую 
таску (локига `FIFO` очереди).

Вне синхронизированного блока запускаем метод `run()` у таска.

ИТОГО 3 варианта поведения:
1. пул активен, задачи есть:
 - поток извлекает задачу из очереди методом `poll()`
 - поток выполняет задачу
 - переходит к следующей итерации цикла `while(...)`
 - ...
2. пул активен, но очередь пустая:
 - поток вызовет `wait()`
 - при добавлении новой задачи, вызывается `notifyAll()`, спящие потоки
пробуждаются.
 - снова проверка условия на пустоту очереди задач.
 - поток извлечет задачу и выполнит ее.
 - ...
3. пул остановлен:
 - если остались в очереди задач таковые, они довыполняются
 - прокнется условие на `return`.

### 1.10.2.4 Метод `shutdown()`
```java
public void shutdown() {
    is_running = false;
    synchronized (this) {
        notifyAll();
    }
    for (Thread thread : threads) {
        thread.interrupt();
    }
    for (Thread thread : threads) {
        try {
            thread.join();
            // все рабочие потоки завершат выполнение своих задач перед тем, как метод shutdown() завершится
            // если поток вызывающий join будет прерван, то будет исключение
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```
Метод для остановки пула потоков. Сразу устанавливаем флаг `is_running = false` 
и пробуждаем все спящие потоки, они снова проверяют условие цикла, а 
поскольку флаг теперь `false`, то потоки завершают работу.  

Далее с помощью цикла `for` все рабочие потоки завершат 
выполнение своих задач перед тем, как метод `shutdown()` завершится
(то есть основной поток ждет завершения "рабочих" потоков 
с помощью метода [join()](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#join--)).

## 2 Класс `FactoryGUI`
```java
public class FactoryGUI extends JFrame {
    private final JSlider body_supplier_speed;
    private final JSlider motor_supplier_speed;
    private final JSlider accessory_supplier_speed;
    private final JSlider dealer_speed;
    
    private final JLabel body_speed_label;
    private final JLabel motor_speed_label;
    private final JLabel accessory_speed_label;
    private final JLabel dealer_speed_label;

    private final JLabel body_storage_label;
    private final JLabel motor_storage_label;
    private final JLabel accessory_storage_label;
    private final JLabel car_storage_label;

    private final JLabel sold_cars_label;
    private final JProgressBar car_storage_pb;

    private final int body_storage_capacity;
    private final int motor_storage_capacity;
    private final int accessory_storage_capacity;
    private final int car_storage_capacity;

    private final List<Supplier<Body>> bodySuppliers;
    private final List<Supplier<Motor>> motorSuppliers;
    private final List<Supplier<Accessory>> accessorySuppliers;
    private final List<Dealer> dealers;

    public FactoryGUI(List<Supplier<Body>> bodySuppliers, List<Supplier<Motor>> motorSuppliers,
                      List<Supplier<Accessory>> accessorySuppliers, List<Dealer> dealers,

                      int body_storage_capacity, int motor_storage_capacity,
                      int accessory_storage_capacity, int car_storage_capacity,

                      int body_supplier_delay, int motor_supplier_delay,
                      int accessory_supplier_delay, int dealer_delay) {
        this.body_storage_capacity = body_storage_capacity;
        this.motor_storage_capacity = motor_storage_capacity;
        this.accessory_storage_capacity = accessory_storage_capacity;
        this.car_storage_capacity = car_storage_capacity;

        this.bodySuppliers = bodySuppliers;
        this.motorSuppliers = motorSuppliers;
        this.accessorySuppliers = accessorySuppliers;
        this.dealers = dealers;
        
        setTitle("Factory control panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel stats_panel = new JPanel();
        stats_panel.setLayout(new GridLayout(6, 1));
        stats_panel.setBorder(new TitledBorder(
                new LineBorder(new Color(70, 130, 180), 4, true),
                "Statistics", 
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(70, 130, 180)
        ));

        body_storage_label = new JLabel("Body storage: 0/" + body_storage_capacity, JLabel.LEFT);
        body_storage_label.setFont(new Font("Arial", Font.BOLD, 14));
        motor_storage_label = new JLabel("Motor storage: 0/" + motor_storage_capacity, JLabel.LEFT);
        motor_storage_label.setFont(new Font("Arial", Font.BOLD, 14));
        accessory_storage_label = new JLabel("Accessory storage: 0/" + accessory_storage_capacity, JLabel.LEFT);
        accessory_storage_label.setFont(new Font("Arial", Font.BOLD, 14));
        car_storage_label = new JLabel("Car storage: 0/" + car_storage_capacity, JLabel.LEFT);
        car_storage_label.setFont(new Font("Arial", Font.BOLD, 14));
        car_storage_pb = new JProgressBar(0, car_storage_capacity);
        car_storage_pb.setStringPainted(true);
        car_storage_pb.setFont(new Font("Arial", Font.ITALIC, 16));
        car_storage_pb.setValue(0);
        car_storage_pb.setString("0/" + car_storage_capacity);

        sold_cars_label = new JLabel("Sold cars: 0", JLabel.LEFT);
        sold_cars_label.setFont(new Font("Arial", Font.BOLD, 14));

        stats_panel.add(body_storage_label);
        stats_panel.add(motor_storage_label);
        stats_panel.add(accessory_storage_label);
        stats_panel.add(car_storage_label);
        stats_panel.add(car_storage_pb);
        stats_panel.add(sold_cars_label);

        add(stats_panel, BorderLayout.CENTER);

        JPanel control_panel = new JPanel();
        control_panel.setLayout(new GridLayout(4, 2));

        // ползунки скоростей + их метки
        body_speed_label = new JLabel("Body supplier delay (ms): " + body_supplier_delay, JLabel.LEFT);
        body_speed_label.setFont(new Font("Arial", Font.BOLD, 14));
        body_supplier_speed = createSlider(100, 10000, body_supplier_delay, body_speed_label);
        body_supplier_speed.setFont(new Font("Arial", Font.BOLD, 11));
        control_panel.add(body_speed_label);
        control_panel.add(body_supplier_speed);

        motor_speed_label = new JLabel("Motor supplier delay (ms): " + motor_supplier_delay, JLabel.LEFT);
        motor_speed_label.setFont(new Font("Arial", Font.BOLD, 14));
        motor_supplier_speed = createSlider(100, 10000, motor_supplier_delay, motor_speed_label);
        motor_supplier_speed.setFont(new Font("Arial", Font.BOLD, 11));
        control_panel.add(motor_speed_label);
        control_panel.add(motor_supplier_speed);

        accessory_speed_label = new JLabel("Accessory supplier delay (ms): " + accessory_supplier_delay, JLabel.LEFT);
        accessory_speed_label.setFont(new Font("Arial", Font.BOLD, 14));
        accessory_supplier_speed = createSlider(100, 10000, accessory_supplier_delay, accessory_speed_label);
        accessory_supplier_speed.setFont(new Font("Arial", Font.BOLD, 11));
        control_panel.add(accessory_speed_label);
        control_panel.add(accessory_supplier_speed);

        dealer_speed_label = new JLabel("Dealer delay (ms): " + dealer_delay, JLabel.LEFT);
        dealer_speed_label.setFont(new Font("Arial", Font.BOLD, 14));
        dealer_speed = createSlider(100, 10000, dealer_delay, dealer_speed_label);
        dealer_speed.setFont(new Font("Arial", Font.BOLD, 11));
        control_panel.add(dealer_speed_label);
        control_panel.add(dealer_speed);

        add(control_panel, BorderLayout.SOUTH);
    }

    private JSlider createSlider(int min, int max, int value, JLabel label) {
        JSlider slider = new JSlider(min, max, value);

        slider.setMajorTickSpacing((max - min) / 10);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        
        slider.addChangeListener(e -> {
            int slider_value = slider.getValue();
            label.setText(label.getText().split(":")[0] + ": " + slider_value);

            if (e.getSource() == body_supplier_speed) {
                for (Supplier<Body> supplier : bodySuppliers) {
                    supplier.setDelay(body_supplier_speed.getValue());
                }
            } else if (e.getSource() == motor_supplier_speed) {
                for (Supplier<Motor> supplier : motorSuppliers) {
                    supplier.setDelay(motor_supplier_speed.getValue());
                }
            } else if (e.getSource() == accessory_supplier_speed) {
                for (Supplier<Accessory> supplier : accessorySuppliers) {
                    supplier.setDelay(accessory_supplier_speed.getValue());
                }
            } else if (e.getSource() == dealer_speed) {
                for (Dealer dealer : dealers) {
                    dealer.setDelay(dealer_speed.getValue());
                }
            }
        });

        return slider;
    }

    public void updateStats(int body_storage, int motor_storage, int accessory_storage, int car_storage, int sold_cars) {
        body_storage_label.setText("Body storage: " + body_storage + "/" + body_storage_capacity);
        motor_storage_label.setText("Motor storage: " + motor_storage + "/" + motor_storage_capacity);
        accessory_storage_label.setText("Accessory storage: " + accessory_storage + "/" + accessory_storage_capacity);
        car_storage_label.setText("Car storage: " + car_storage + "/" + car_storage_capacity);
        sold_cars_label.setText("Sold cars: " + sold_cars);
        car_storage_pb.setValue(car_storage);
        car_storage_pb.setString(car_storage + "/" + car_storage_capacity);
    }
}
```
Класс представляет графический интерфейс для управления фабрикой
производства автомобилей. Также отображается информация о производстве
и продаже автомобилей.

Мне впадлу описывать GUI класс поэтому оговорю один технический нюанс:  
в классе `Main` у нас были такие три локальных массива:
```java
List<Supplier<Body>> body_suppliers = new ArrayList<>();
List<Supplier<Motor>> motor_suppliers = new ArrayList<>();
List<Supplier<Accessory>> accessory_suppliers = new ArrayList<>();
```
Их единственный функционал это сбор соответствующих поставщиков и 
подача их в конструктор класса `FactoryGUI`.  
Так вот здесь именно их задержки мы будем изменять с помощью ползунков.
При создании каждого ползунка мы добавляли ему функциональный 
интерфейс [ChangeListener](https://docs.oracle.com/javase/7/docs/api/javax/swing/event/ChangeListener.html) 
с помощью метода `addChangeListener(...)`:
```java
slider.addChangeListener(e -> {
    int slider_value = slider.getValue();
    label.setText(label.getText().split(":")[0] + ": " + slider_value);

    if (e.getSource() == body_supplier_speed) {
        for (Supplier<Body> supplier : bodySuppliers) {
            supplier.setDelay(body_supplier_speed.getValue());
        }
    } else if (e.getSource() == motor_supplier_speed) {
        for (Supplier<Motor> supplier : motorSuppliers) {
            supplier.setDelay(motor_supplier_speed.getValue());
        }
    } else if (e.getSource() == accessory_supplier_speed) {
        for (Supplier<Accessory> supplier : accessorySuppliers) {
            supplier.setDelay(accessory_supplier_speed.getValue());
        }
    } else if (e.getSource() == dealer_speed) {
        for (Dealer dealer : dealers) {
            dealer.setDelay(dealer_speed.getValue());
        }
    }
});
```
В качестве проверки в консоль выводил текущие значения задержи поставщиков
и дилеров. Они действительно менялись.
В качестве проверки также логично попробовать поиграться с ползунками:
 - сделайте значение задержки дилеров кратно меньше задержек поставщиков. 
Тогда получите что хранилище машин начнет опустошаться
 - сделайте наоборот и хранилище машин почти всегда будет полным. 

HAWk 2A