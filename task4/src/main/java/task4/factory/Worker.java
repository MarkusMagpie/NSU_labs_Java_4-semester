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
//        try {
//            while (!Thread.currentThread().isInterrupted()) {
//                Body body = body_storage.get();
//                Motor motor = motor_storage.get();
//                Accessory accessory = accessory_storage.get();
//
//                Car car = new Car(++car_counter, body, motor, accessory);
//                car_storage.add(car);
////                System.out.println("Assembled: " + car.toString() + " with id " + car.getId());
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Body body = body_storage.get();
                Motor motor = motor_storage.get();
                Accessory accessory = accessory_storage.get();

                Car car = new Car(++car_counter, body, motor, accessory);
                car_storage.add(car);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}