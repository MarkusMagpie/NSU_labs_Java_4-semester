package task2.commands;

import task2.Command;
import task2.ExecutionContext;

public class DefineCommand extends Command {
    @Override
    public void Execute(ExecutionContext context, String[] args) {
        if (args.length != 2) {
            // https://metanit.com/java/tutorial/4.2.php - про исключения в целом
            throw new IllegalArgumentException("Команда DEFINE принимает два аргумента!");
        }

        String variable = args[0];
        double value = Double.parseDouble(args[1]);
        context.GetVariables().put(variable, value);
    }
}
