package task4.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

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
//        System.out.println("\nNew Dealer delay: " + delay);
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
//                System.out.println("\nDealer's delay: " + delay);
                Car car = car_storage.get();
                ++sold_cars;
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
    }

    public int getSoldCars() {
        return sold_cars;
    }
}