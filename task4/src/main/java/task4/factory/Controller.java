package task4.factory;

import task4.threadpool.Task;
import task4.threadpool.ThreadPool;

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
        // изначально заполняем пул задач до заполнения склада
        int initialTasks = capacity - carStorage.getCurrentSize();
        for (int i = 0; i < initialTasks; i++) {
            threadPool.addTask(new Task(new Worker(bodyStorage, motorStorage, accessoryStorage, carStorage)));
        }

        // затем контроллер засыпает и ждет, когда будет продана машина (из хранилища)
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (carStorage) {
                try {
                    // ожидаем уведомления о продаже машины дилером
                    // после добавления автомобиля на склад рабочим вызывается car_storage.notifyAll()
                    carStorage.wait();

                    // будим всех рабочих при появлении места
                    if (!carStorage.isFull()) {
                        carStorage.notifyAll();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
