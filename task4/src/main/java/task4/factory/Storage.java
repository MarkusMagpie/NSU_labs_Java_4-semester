package task4.factory;

import java.util.LinkedList;
import java.util.Queue;

class Storage<T extends Part> {
    private final Queue<T> items = new LinkedList<>(); // LinkedList реализует интерфейс Queue
    private final int capacity;

    public Storage(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void add(T item) throws InterruptedException {
        while (items.size() >= capacity) {
            System.out.println("Storage is full for " + item.getClass().getSimpleName());
            wait();
        }
        //  while - для повторной проверки условия после пробуждения потока

        items.add(item);
        System.out.println("Added to storage: " + item + " with id " + item.getId() + " by " + Thread.currentThread().getName());
        notifyAll();
    }
    // synchronized - только один поток может выполнять синхронизированный метод одновременно.

    public synchronized T get() throws InterruptedException {
        while (items.isEmpty()) {
            wait();
        }

        T item = items.poll();
        notifyAll();

        return item;
    }
}
