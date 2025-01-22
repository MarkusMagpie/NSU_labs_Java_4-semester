package task2.commands;

import task2.Command;
import task2.ExecutionContext;

public class PushCommand extends Command {
    @Override
    public void Execute(ExecutionContext context, String[] args) {
        if (args.length != 1) {
            // https://metanit.com/java/tutorial/4.2.php - про исключения в целом
            throw new IllegalArgumentException("Команда PUSH принимает только один аргумент!");
        }

        // значение аргумента кладется на стек
        String arg = args[0];
//        System.out.println(arg);
//        System.out.println(context.GetVariables().get(arg));
        double value = context.GetVariables().get(arg);
        // есть ли значение arg в таблице переменных variables?
        // - да, то берем его
        // - нет, переменная отсутствует, тогда arg пытается интерпретироваться как число.
        // Пример: arg = "x", variables = { "x": 10.0 } -> value = 10.0
        context.GetStack().push(value);
    }
}
