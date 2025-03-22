package task4.factory;

import task4.gui.FactoryGUI;
import task4.threadpool.*;

import javax.swing.*;
import javax.swing.Timer;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader("task4\\src\\main\\resources\\config.properties");

        List<Supplier<Body>> body_suppliers = new ArrayList<>();
        List<Supplier<Motor>> motor_suppliers = new ArrayList<>();
        List<Supplier<Accessory>> accessory_suppliers = new ArrayList<>();
        List<Dealer> dealers = new ArrayList<>();

//        List<Thread> supplierThreads = new ArrayList<>();
//        List<Thread> dealerThreads = new ArrayList<>();

        Storage<Body> bodyStorage = new Storage<>(configLoader.getStorageBodySize());
        Storage<Motor> motorStorage = new Storage<>(configLoader.getStorageMotorSize());
        Storage<Accessory> accessoryStorage = new Storage<>(configLoader.getStorageAccessorySize());
        Storage<Car> carStorage = new Storage<>(configLoader.getStorageCarSize());

        // запуск поставщиков кузовов в отдельных потоках
        int body_supplier_delay = 4000;
        for (int i = 0; i < configLoader.getBodySuppliers(); ++i) {
            Supplier<Body> supplier = new Supplier<>(bodyStorage, body_supplier_delay, Body.class);
//            supplierThreads.add(thread);
            body_suppliers.add(supplier);
            Thread thread = new Thread(supplier);
            thread.start();
        }

        // задачи поставщиков двигателей
        int motor_supplier_delay = 4000;
        for (int i = 0; i < configLoader.getMotorSuppliers(); ++i) {
            Supplier<Motor> supplier = new Supplier<>(motorStorage, motor_supplier_delay, Motor.class);
//            supplierThreads.add(thread);
            motor_suppliers.add(supplier);
            Thread thread = new Thread(supplier);
            thread.start();
        }

        // задачи поставщиков аксессуаров
        int accessory_supplier_delay = 4000;
        for (int i = 0; i < configLoader.getAccessorySuppliers(); ++i) {
            Supplier<Accessory> supplier = new Supplier<>(accessoryStorage, accessory_supplier_delay, Accessory.class);
//            supplierThreads.add(thread);
            accessory_suppliers.add(supplier);
            Thread thread = new Thread(supplier);
            thread.start();
        }

        // пул потоков ONLY для рабочих
        ThreadPool threadPool = new ThreadPool(configLoader.getWorkers());

        // задачи сборщиков cars - рабочих
//        for (int i = 0; i < configLoader.getWorkers(); ++i) {
//            threadPool.addTask(new Task(new Worker(bodyStorage, motorStorage, accessoryStorage, carStorage)));
//        }

        Controller controller = new Controller(carStorage, threadPool, configLoader.getStorageCarSize(),
                bodyStorage, motorStorage, accessoryStorage);
        new Thread(controller).start();

        try (FileWriter writer = new FileWriter("task4\\src\\main\\resources\\factorylog.txt", false)) {
        } catch (Exception e) {
            System.out.println("Failed to clear log file: " + e.getMessage());
        }

        // задачи дилеров
        int dealer_delay = 5000;
        for (int i = 0; i < configLoader.getDealers(); ++i) {
            Dealer dealer = new Dealer(carStorage, dealer_delay, configLoader.isLogEnabled());
            Thread thread = new Thread(dealer);
            dealers.add(dealer);
//            dealerThreads.add(thread);
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

//        try {
//            Thread.sleep(15000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        System.out.println("\nFactory finished working for requested time. Waiting for threads to finish...\n");
//
//        for (Thread thread : supplierThreads) {
//            thread.interrupt();
//        }
//        for (Thread thread : dealerThreads) {
//            thread.interrupt();
//        }
//
//        System.out.println("\nSupplier and dealer threads finished. Waiting for thread pool to finish...\n");
//
//        // завершаем работу пула потоков
//        threadPool.shutdown();
//
//        gui.setVisible(false);
//        timer.stop();
//
//        System.out.println("\nThread pool finished. Exiting...\n");
//
//        System.exit(0);
    }
}
