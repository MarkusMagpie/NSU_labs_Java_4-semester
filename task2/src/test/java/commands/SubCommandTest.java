package commands;

import org.junit.jupiter.api.Test;

import task2.ExecutionContext;
import task2.commands.SubCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubCommandTest {
    @Test
    void testSub() {
        ExecutionContext context = new ExecutionContext();
        SubCommand subCommand = new SubCommand();

        // firstly push 5 and then 3, when I pop, it will be 5 - 3 = 2
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
