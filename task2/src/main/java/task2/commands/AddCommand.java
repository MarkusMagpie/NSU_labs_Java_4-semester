package task2.commands;

import task2.Command;
import task2.ExecutionContext;

public class AddCommand implements Command {
    public void Execute(ExecutionContext context, String[] args) {
        if (context.GetStack().size() < 2) {
            throw new IllegalArgumentException("Команда ADD принимает два аргумента. Аргументов не хватает.");
        }

        // беру попом из стека два числа, суммирую их и кладу на стек результат
        double b = context.GetStack().pop();
        double a = context.GetStack().pop();
        double res = a + b;

        if (Double.isInfinite(res) || Double.isNaN(res) || res >= Double.MAX_VALUE || res <= Double.MIN_VALUE) {
            context.GetStack().push(a);
            context.GetStack().push(b);
            throw new ArithmeticException("Переполнение Add: значение переменной выходит за допустимый диапазон чисел double");
        }

        context.GetStack().push(res);
    }
}
