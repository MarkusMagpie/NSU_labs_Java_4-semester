package task4.threadpool;

public class Task {
    private final Runnable task;

    public Task(Runnable task) {
        this.task = task;
    }

    public void execute() {
        task.run();
    }
}