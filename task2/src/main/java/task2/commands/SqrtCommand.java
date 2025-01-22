package task2.commands;

import task2.Command;
import task2.ExecutionContext;

import java.util.EmptyStackException;

public class SqrtCommand extends Command {
    @Override
    public void Execute(ExecutionContext context, String[] args) {
        if (context.GetStack().isEmpty()) { // запомнт блять наконец уже что стек ПУСТОЙ -> НИХУЯ не выводим
            throw new EmptyStackException();
        }

        double a = context.GetStack().pop();
        if (a < 0) {
            throw new ArithmeticException("Команда Sqrt принимает только положительные числа!");
        }

        context.GetStack().push(Math.sqrt(a));
    }
}
