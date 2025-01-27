package task4.factory;

import task4.threadpool.*;


public class Main {
    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader("task4\\src\\main\\resources\\config.properties");

        // Создаем хранилища для всех трех видов деталей с вместительностью
        //   из конфигурационного файла. + еще одно хранилище для автомобилей
        Storage<Body> bodyStorage = new Storage<>(configLoader.getStorageBodySize());
        Storage<Motor> motorStorage = new Storage<>(configLoader.getStorageMotorSize());
        Storage<Accessory> accessoryStorage = new Storage<>(configLoader.getStorageAccessorySize());
        Storage<Car> carStorage = new Storage<>(configLoader.getStorageCarSize());

        // пул потоков для выполнения задач
        ThreadPool threadPool = new ThreadPool(configLoader.getWorkers());

        // добавляем задачи поставщиков кузовов
        for (int i = 0; i < configLoader.getBodySuppliers(); ++i) {
            threadPool.addTask(new Task(() -> {
                Supplier<Body> supplier = new Supplier<>(bodyStorage, 500, Body.class);
                supplier.run();
            }));
        }

        // задачи поставщиков двигателей
        for (int i = 0; i < configLoader.getMotorSuppliers(); ++i) {
            threadPool.addTask(new Task(() -> {
                Supplier<Motor> supplier = new Supplier<>(motorStorage, 500, Motor.class);
                supplier.run();
            }));
        }

        // задачи поставщиков аксессуаров
        for (int i = 0; i < configLoader.getAccessorySuppliers(); ++i) {
            threadPool.addTask(new Task(() -> {
                Supplier<Accessory> supplier = new Supplier<>(accessoryStorage, 500, Accessory.class);
                supplier.run();
            }));
        }

        // задачи сборщиков cars - рабочих
        for (int i = 0; i < configLoader.getWorkers(); ++i) {
            threadPool.addTask(new Task(() -> {
                Worker worker = new Worker(bodyStorage, motorStorage, accessoryStorage, carStorage);
                worker.run();
            }));
        }

        // задачи дилеров
        for (int i = 0; i < configLoader.getDealers(); ++i) {
            threadPool.addTask(new Task(() -> {
                Dealer dealer = new Dealer(carStorage, 1000, configLoader.isLogEnabled());
                dealer.run();
            }));
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            threadPool.shutdown();
        }));

        System.out.println("Factory is running...");
    }
}
