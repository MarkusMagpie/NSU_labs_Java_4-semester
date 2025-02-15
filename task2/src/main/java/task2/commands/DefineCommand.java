package task2.commands;

import task2.Command;
import task2.ExecutionContext;

import java.lang.reflect.Type;

public class DefineCommand implements Command {
    @Override
    public void Execute(ExecutionContext context, String[] args) {
        if (args.length != 2) {
            // https://metanit.com/java/tutorial/4.2.php - про исключения в целом
            throw new IllegalArgumentException("Команда DEFINE принимает два аргумента!");
        }

        String variable = args[0];
        if (variable.matches("[0-9]+")) {
            throw new IllegalArgumentException("Имя переменной не может быть числом!");
        }

        double value = Double.parseDouble(args[1]);
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            throw new ArithmeticException("Переполнение в define: значение переменной выходит за допустимый диапазон");
        }
        context.GetVariables().put(variable, value);
    }
}
