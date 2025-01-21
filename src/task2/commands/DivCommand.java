package task2.commands;

import task2.Command;
import task2.ExecutionContext;

public class DivCommand extends Command {
    @Override
    public void Execute(ExecutionContext context, String[] args) {
        if (context.GetStack().size() < 2) {
            throw new IllegalArgumentException("Команда Div принимает два аргумента. Аргументов не хватает.");
        }

        double b = context.GetStack().pop();
        double a = context.GetStack().pop();
        context.GetStack().push(a / b);
    }
}
