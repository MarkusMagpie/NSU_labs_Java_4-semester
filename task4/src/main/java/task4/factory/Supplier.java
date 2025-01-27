package task4.factory;

class Supplier<T extends Part> implements Runnable {
    private final Storage<T> storage;
    private final int delay; // задержка (в миллисекундах) между созданием и добавлением каждой детали
    private final Class<T> part_type; // класс типа T(Body, Monitor или Accessory), который будет создаваться
    private static int id_counter = 0;

    public Supplier(Storage<T> storage, int delay, Class<T> part_type) {
        this.storage = storage;
        this.delay = delay;
        this.part_type = part_type;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                T part = createPart();
                storage.add(part);
                System.out.println("Supplier produced: " + part + " with id " + part.getId());
                Thread.sleep(delay); // между созданием деталей должна быть воображаемая задержка
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public T createPart() {
        try {
            return part_type.getConstructor(Integer.TYPE).newInstance(++id_counter);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create part " + part_type + " with id " + id_counter, e);
        }
    }
}
