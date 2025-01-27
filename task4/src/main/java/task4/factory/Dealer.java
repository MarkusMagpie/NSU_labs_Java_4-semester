package task4.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.PrintWriter;

class Dealer implements Runnable {
    private static final Logger logger = LogManager.getLogger(Dealer.class);

    private final Storage<Car> car_storage;
    private final int delay;

    private final boolean log_enabled;

    public Dealer(Storage<Car> car_storage, int delay, boolean log_enabled) {
        this.car_storage = car_storage;
        this.delay = delay;
        this.log_enabled = log_enabled;
    }

    @Override
    public void run() {
//        try {
//            while (!Thread.currentThread().isInterrupted()) {
//                Car car = car_storage.get();
//                System.out.println("Sold: " + car.toString() + " with id " + car.getId());
//                if (log_enabled) { writeLog(car); }
//                Thread.sleep(delay);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Car car = car_storage.get();
                System.out.println("Sold: " + car.toString() + " with id " + car.getId());
                if (log_enabled) { writeLog(car); }
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void writeLog(Car car) {
        String message = String.format("%d: Dealer: Car %d (Body: %d, Motor: %d, Accessory: %d)",
                System.currentTimeMillis(), car.getId(),
                car.getBody().getId(), car.getMotor().getId(),
                car.getAccessory().getId());

        logger.info(message);
//        System.out.println(message);
    }
}