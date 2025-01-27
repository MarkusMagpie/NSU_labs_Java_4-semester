package task4.threadpool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ThreadPool {
    private final Queue<Task> task_queue = new LinkedList<>();
    private final List<Thread> workers;
    private volatile boolean is_running = true;

    public ThreadPool(int num_threads) {
        workers = new ArrayList<>();
        for (int i = 0; i < num_threads; i++) {
            Thread worker = new Thread(this::workerLoop, "worker-" + i);
            workers.add(worker);
            worker.start();
        }
    }

    public synchronized void addTask(Task task) {
        task_queue.add(task);
        notify();
    }

    private void workerLoop() {
        while (is_running) {
            Task task;
            synchronized (this) {
                while (task_queue.isEmpty() && is_running) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (task_queue.isEmpty() && !is_running) {
                    return;
                }
                task = task_queue.poll();
            }
            if (task != null) {
                task.execute();
            }
        }
    }

    public void shutdown() {
        is_running = false;
        synchronized (this) {
            notifyAll();
        }
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
