package task2.commands;

import task2.Command;
import task2.ExecutionContext;

public class SubCommand extends Command {
    @Override
    public void Execute(ExecutionContext context, String[] args) {
        if (context.GetStack().size() < 2) {
            throw new IllegalArgumentException("Команда SUB принимает два аргумента. Аргументов не хватает.");
        }

        double b = context.GetStack().pop();
        double a = context.GetStack().pop();
        context.GetStack().push(a - b);
    }
}
