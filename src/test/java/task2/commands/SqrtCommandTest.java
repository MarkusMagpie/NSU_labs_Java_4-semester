package task2.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import task2.ExecutionContext;

import java.util.EmptyStackException;

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
