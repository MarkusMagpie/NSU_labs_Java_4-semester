package task2.commands;

import task2.Command;
import task2.ExecutionContext;

import java.util.EmptyStackException;

public class PopCommand implements Command {
    public void Execute(ExecutionContext context, String[] args) {
        if (context.GetStack().isEmpty()) {
            throw new EmptyStackException();
//            throw new IllegalArgumentException("Стек пуст!");
        }

        context.GetStack().pop();
    }
}
