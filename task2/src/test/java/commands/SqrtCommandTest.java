package commands;

import org.junit.jupiter.api.Test;

import task2.ExecutionContext;
import task2.commands.SqrtCommand;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SqrtCommandTest {
    @Test
    void testSqrt() {
        ExecutionContext context = new ExecutionContext();
        SqrtCommand sqrtCommand = new SqrtCommand();

        context.GetStack().push(16.0);

        sqrtCommand.Execute(context, new String[]{});

        assertEquals(1, context.GetStack().size());
        assertEquals(4.0, context.GetStack().peek());
    }

    @Test
    void testSqrtNegativeNumber() {
        ExecutionContext context = new ExecutionContext();
        SqrtCommand sqrtCommand = new SqrtCommand();

        context.GetStack().push(-9.0);

        assertThrows(ArithmeticException.class, () -> sqrtCommand.Execute(context, new String[]{}));
    }

    @Test
    void testSqrtEmptyStack() {
        ExecutionContext context = new ExecutionContext();
        SqrtCommand sqrtCommand = new SqrtCommand();

        assertThrows(EmptyStackException.class, () -> sqrtCommand.Execute(context, new String[]{}));
    }
}
