package task4.factory;

public class Supplier<T extends Part> implements Runnable {
    private final Storage<T> storage;
    private volatile int delay; // задержка (в миллисекундах) между созданием и добавлением каждой детали
    private final Class<T> part_type; // класс типа T(Body, Monitor или Accessory), который будет создаваться
    private static int id_counter = 0;

    public Supplier(Storage<T> storage, int delay, Class<T> part_type) {
        this.storage = storage;
        this.delay = delay;
        this.part_type = part_type;
    }

    public void setDelay(int delay) {
//        System.out.println("\nNew Supplier delay: " + delay);
        this.delay = delay;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                T part = createPart();
                storage.add(part);
                Thread.sleep(delay); // между созданием деталей должна быть воображаемая задержка
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

//        while (!Thread.currentThread().isInterrupted()) {
//            try {
//                T part = createPart();
//                storage.add(part);
//                Thread.sleep(delay);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }
    }

    public T createPart() {
        try {
            // Reflection is a mechanism for studying data about a program during its execution.
            return part_type.getConstructor(int.class).newInstance(++id_counter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create part " + part_type + " with id " + id_counter, e);
        }
    }
}
