package task2.commands;

import task2.Command;
import task2.ExecutionContext;

import java.util.EmptyStackException;

public class SqrtCommand implements Command {
    public void Execute(ExecutionContext context, String[] args) {
        if (context.GetStack().isEmpty()) {
            throw new EmptyStackException();
        }

        double a = context.GetStack().pop();
        if (a < 0) {
            // возвращаем число в стек
            context.GetStack().push(a);
            throw new ArithmeticException("Команда Sqrt принимает только положительные числа!");
        }

        if (Double.isInfinite(a) || Double.isNaN(a) || a >= Double.MAX_VALUE || a <= Double.MIN_VALUE) {
            context.GetStack().push(a);
            throw new ArithmeticException("Переполнение Sqrt: значение переменной выходит за допустимый диапазон чисел double");
        }

        double res = Math.sqrt(a);

        context.GetStack().push(res);
    }
}
