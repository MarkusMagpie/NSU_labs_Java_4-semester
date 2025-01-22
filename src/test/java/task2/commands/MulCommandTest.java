package task2.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import task2.ExecutionContext;

public class MulCommandTest {
    @Test
    void testMul() {
        ExecutionContext context = new ExecutionContext();
        MulCommand mulCommand = new MulCommand();

        context.GetStack().push(5.0);
        context.GetStack().push(3.0);

        mulCommand.Execute(context, new String[]{});

        assertEquals(1, context.GetStack().size());
        assertEquals(15.0, context.GetStack().peek());
    }

    @Test
    void testMulInsufficientOperands() {
        ExecutionContext context = new ExecutionContext();
        MulCommand mulCommand = new MulCommand();

        context.GetStack().push(5.0);

        assertThrows(IllegalArgumentException.class, () -> mulCommand.Execute(context, new String[]{}));
    }
}
