package task2.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import task2.ExecutionContext;

public class SubCommandTest {
    @Test
    void testSub() {
        ExecutionContext context = new ExecutionContext();
        SubCommand subCommand = new SubCommand();

        // сначала кладу 5 затем 3, когда я извлеку то будет 5 - 3 = 2
        context.GetStack().push(5.0);
        context.GetStack().push(3.0);

        subCommand.Execute(context, new String[]{});

        assertEquals(1, context.GetStack().size());
        assertEquals(2.0, context.GetStack().peek());
    }

    @Test
    void testSubInsufficientOperands() {
        ExecutionContext context = new ExecutionContext();
        SubCommand subCommand = new SubCommand();

        context.GetStack().push(5.0);

        assertThrows(IllegalArgumentException.class, () -> subCommand.Execute(context, new String[]{}));
    }
}
