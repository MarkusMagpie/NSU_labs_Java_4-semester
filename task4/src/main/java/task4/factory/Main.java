package task4.factory;

import task4.threadpool.*;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader("task4\\src\\main\\resources\\config.properties");

        List<Thread> supplierThreads = new ArrayList<>();
        List<Thread> dealerThreads = new ArrayList<>();

        // Создаем хранилища для всех трех видов деталей с вместительностью
        //   из конфигурационного файла. + еще одно хранилище для автомобилей
        Storage<Body> bodyStorage = new Storage<>(configLoader.getStorageBodySize());
        Storage<Motor> motorStorage = new Storage<>(configLoader.getStorageMotorSize());
        Storage<Accessory> accessoryStorage = new Storage<>(configLoader.getStorageAccessorySize());
        Storage<Car> carStorage = new Storage<>(configLoader.getStorageCarSize());

        // запуск поставщиков кузовов в отдельных потоках
        for (int i = 0; i < configLoader.getBodySuppliers(); ++i) {
//            new Thread(new Supplier<>(bodyStorage, 500, Body.class)).start();
            Supplier<Body> supplier = new Supplier<>(bodyStorage, 500, Body.class);
            Thread thread = new Thread(supplier);
            supplierThreads.add(thread);
            thread.start();
        }

        // задачи поставщиков двигателей
        for (int i = 0; i < configLoader.getMotorSuppliers(); ++i) {
//            new Thread(new Supplier<>(motorStorage, 500, Motor.class)).start();
            Supplier<Motor> supplier = new Supplier<>(motorStorage, 500, Motor.class);
            Thread thread = new Thread(supplier);
            supplierThreads.add(thread);
            thread.start();
        }

        // задачи поставщиков аксессуаров
        for (int i = 0; i < configLoader.getAccessorySuppliers(); ++i) {
//            new Thread(new Supplier<>(accessoryStorage, 500, Accessory.class)).start();
            Supplier<Accessory> supplier = new Supplier<>(accessoryStorage, 500, Accessory.class);
            Thread thread = new Thread(supplier);
            supplierThreads.add(thread);
            thread.start();
        }

        // пул потоков ONLY для рабочих
        ThreadPool threadPool = new ThreadPool(configLoader.getWorkers());

        // задачи сборщиков cars - рабочих
        for (int i = 0; i < configLoader.getWorkers(); ++i) {
            threadPool.addTask(new Task(new Worker(bodyStorage, motorStorage, accessoryStorage, carStorage)));
        }

        try (FileWriter writer = new FileWriter("task4\\src\\main\\resources\\factorylog.txt", false)) {
        } catch (Exception e) {
            System.out.println("Failed to clear log file: " + e.getMessage());
        }

        // задачи дилеров
        for (int i = 0; i < configLoader.getDealers(); ++i) {
//            new Thread(new Dealer(carStorage, 1000, configLoader.isLogEnabled())).start();
            Dealer dealer = new Dealer(carStorage, 1000, configLoader.isLogEnabled());
            Thread thread = new Thread(dealer);
            dealerThreads.add(thread);
            thread.start();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nFactory finished working for requested time. Waiting for threads to finish...\n");

        for (Thread thread : supplierThreads) {
            thread.interrupt();
        }
        for (Thread thread : dealerThreads) {
            thread.interrupt();
        }

//        // ожидаем завершение работы поставщиков и дилеров
//        for (Thread thread : supplierThreads) {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//
//        for (Thread thread : dealerThreads) {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }

        System.out.println("\nSupplier and dealer threads finished. Waiting for thread pool to finish...\n");

        // завершаем работу пула потоков
        threadPool.shutdown();

        System.out.println("\nThread pool finished. Exiting...\n");
    }
}
