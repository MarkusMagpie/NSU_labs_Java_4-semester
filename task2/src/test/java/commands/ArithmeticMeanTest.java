package commands;

import org.junit.jupiter.api.Test;
import task2.ExecutionContext;
import task2.commands.ArithmeticMeanCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArithmeticMeanTest {
    // тест без аргументов - должна обработаться весь стек
    @Test
    void testARITHM_AllStack() {
        System.out.println("Тест ARITHM1 - без аргументов - должна обработаться весь стек");
        ExecutionContext context = new ExecutionContext();
        ArithmeticMeanCommand cmd = new ArithmeticMeanCommand();

        context.GetStack().push(6.0);
        assertEquals(1, context.GetStack().size());
        assertEquals(6.0, context.GetStack().peek());

        context.GetStack().push(4.0);
        assertEquals(2, context.GetStack().size());
        assertEquals(4.0, context.GetStack().peek());

        context.GetStack().push(2.0);
        assertEquals(3, context.GetStack().size());
        assertEquals(2.0, context.GetStack().peek());

        cmd.Execute(context, new String[0]);
        assertEquals(1, context.GetStack().size());
        assertEquals(4.0, context.GetStack().peek(), 1e-9);
    }

    @Test
    void testARITHM_WithArgument() {
        System.out.println("Тест ARITHM2 - с аргументом - должна обработаться только первые n элементов стека");
        ExecutionContext context = new ExecutionContext();
        ArithmeticMeanCommand cmd = new ArithmeticMeanCommand();

        context.GetStack().push(10.0);
        context.GetStack().push(20.0);
        context.GetStack().push(30.0);
        context.GetStack().push(40.0);
        // n = 3 -беру 3 чиселки: (40+30+20)/3 = 30
        cmd.Execute(context, new String[]{"3"});

        assertEquals(2, context.GetStack().size());
        assertEquals(30.0, context.GetStack().peek(), 1e-9);
    }

    @Test
    void testARITHM_NegativeArgument() {
        System.out.println("Тест ARITHM3 - если передал n<0, должно выброситься исключение");
        ExecutionContext context = new ExecutionContext();
        ArithmeticMeanCommand cmd = new ArithmeticMeanCommand();

        context.GetStack().push(1.0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cmd.Execute(context, new String[]{"-1"}));
        assertEquals("ARIFM: количество чисел должно быть положительным.", exception.getMessage());
    }

    @Test
    void testARITHM_ZeroArgument() {
        System.out.println("Тест ARITHM4 - если передал n=0, должно выброситься исключение");
        ExecutionContext context = new ExecutionContext();
        ArithmeticMeanCommand cmd = new ArithmeticMeanCommand();

        context.GetStack().push(1.0);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cmd.Execute(context, new String[]{"0"}));
        assertEquals("ARIFM: количество чисел должно быть положительным.", exception.getMessage());
    }

    @Test
    void testARITHM_InsufficientStack() {
        System.out.println("Тест ARITHM5 - на стеке впринципе недостаточно чисел для обработки");
        ExecutionContext context = new ExecutionContext();
        ArithmeticMeanCommand cmd = new ArithmeticMeanCommand();

        context.GetStack().push(5.0);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cmd.Execute(context, new String[]{"2"}));
        assertEquals("ARIFM: на стеке недостаточно чисел. Требуется 2, найдено 1.", exception.getMessage());
    }

    @Test
    void testARITHM_ArithmeticOverflow() {
        ExecutionContext context = new ExecutionContext();
        ArithmeticMeanCommand cmd = new ArithmeticMeanCommand();

        context.GetStack().push(Double.MAX_VALUE);
        context.GetStack().push(Double.MAX_VALUE);
        ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> cmd.Execute(context, new String[]{"2"}));

        assertEquals("Переполнение Arifm: значение переменной выходит за допустимый диапазон.", exception.getMessage());
        assertEquals(2, context.GetStack().size());
        assertEquals(Double.MAX_VALUE, context.GetStack().peek());
    }
}


