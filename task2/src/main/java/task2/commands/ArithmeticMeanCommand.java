package task2.commands;

import task2.Command;
import task2.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

public class ArithmeticMeanCommand implements Command {
    public void Execute(ExecutionContext context, String[] args) {
        int n;
        if (args.length == 0) {
            // берем все числа из стека
            n = context.GetStack().size();
        } else {
            n = Integer.parseInt(args[0]);
        }

        if (n <= 0) {
            throw new IllegalArgumentException("ARIFM: количество чисел должно быть положительным.");
        }

        if (context.GetStack().size() < n) {
            throw new IllegalArgumentException("ARIFM: на стеке недостаточно чисел. Требуется " + n + ", найдено " + context.GetStack().size() + ".");
        }

        // попаем из стека n чисел
        List<Double> stack_numbers = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            stack_numbers.add(context.GetStack().pop());
        }

        double sum = 0;
        for (Double num : stack_numbers) {
            sum += num;
        }
        double avg = sum / n;

        if (Double.isInfinite(avg) || Double.isNaN(avg)) {
            for (int i = stack_numbers.size() - 1; i >= 0; i--) {
                context.GetStack().push(stack_numbers.get(i));
            }
            throw new ArithmeticException("Переполнение Arifm: значение переменной выходит за допустимый диапазон.");
        }

        context.GetStack().push(avg);
    }
}
