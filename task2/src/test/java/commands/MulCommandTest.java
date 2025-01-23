package commands;

import org.junit.jupiter.api.Test;

import task2.ExecutionContext;
import task2.commands.MulCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testMulThrows() {
        ExecutionContext context = new ExecutionContext();
        MulCommand mulCommand = new MulCommand();

        context.GetStack().push(5.0);

        assertThrows(IllegalArgumentException.class, () -> mulCommand.Execute(context, new String[]{}));
    }
}
