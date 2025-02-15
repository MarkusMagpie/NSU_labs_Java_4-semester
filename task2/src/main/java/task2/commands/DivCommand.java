package task2.commands;

import task2.Command;
import task2.ExecutionContext;

public class DivCommand implements Command {
    public void Execute(ExecutionContext context, String[] args) {
        if (context.GetStack().size() < 2) {
            throw new IllegalArgumentException("Команда Div принимает два аргумента. Аргументов не хватает.");
        }

        double b = context.GetStack().pop();
        double a = context.GetStack().pop();

        if (b == 0) {
            // возвращаем оба числа в стек
            context.GetStack().push(a);
            context.GetStack().push(b);
            throw new ArithmeticException("Деление на ноль");
        }

        double res = a / b;
        if (Double.isInfinite(res) || Double.isNaN(res)) {
            context.GetStack().push(a);
            context.GetStack().push(b);
            throw new ArithmeticException("Переполнение Div: значение переменной выходит за допустимый диапазон чисел double");
        }

        context.GetStack().push(a / b);
    }
}
