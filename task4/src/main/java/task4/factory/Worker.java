package task4.factory;

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
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // ждем пока есть место на складе машин
                synchronized (car_storage) {
                    while (car_storage.isFull()) {
                        car_storage.wait();
                    }
                }

                Body body = body_storage.get();
                Motor motor = motor_storage.get();
                Accessory accessory = accessory_storage.get();

                Car car = new Car(++car_counter, body, motor, accessory);

                // добавляем машину на склад + уведомляем контроллер
                synchronized (car_storage) {
                    car_storage.add(car);
                    car_storage.notifyAll();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}