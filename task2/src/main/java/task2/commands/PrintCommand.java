package task2.commands;

import task2.Command;
import task2.ExecutionContext;

import java.util.EmptyStackException;

public class PrintCommand implements Command {
    public void Execute(ExecutionContext context, String[] args) {
        if (context.GetStack().isEmpty()) { // ну если стек пустой то и выводить очевидно нечево
            throw new EmptyStackException();
        }
        System.out.println(context.GetStack().peek());
    }
}
