package task2;

// абстрактный класс команды (будет переопределено в дочерних классах)
// https://www.w3schools.com/java/java_abstract.asp
public abstract class Command {
    public abstract void Execute(ExecutionContext context, String[] args) throws Exception;
}