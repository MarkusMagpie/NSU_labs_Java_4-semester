package task2;

public interface Command {
    void Execute(ExecutionContext context, String[] args) throws Exception;
}