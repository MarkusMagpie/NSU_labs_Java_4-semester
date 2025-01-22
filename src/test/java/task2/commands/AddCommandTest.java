package task2.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import task2.ExecutionContext;

public class AddCommandTest {
    @Test
    void testAdd() {
        ExecutionContext context = new ExecutionContext();
        AddCommand add_command = new AddCommand();

        context.GetStack().push(5.0);
        context.GetStack().push(3.0);

        add_command.Execute(context, new String[]{});

        assertEquals(1, context.GetStack().size());
        assertEquals(8.0, context.GetStack().peek());
    }

    @Test
    void testAddInsufficientOperands() {
        ExecutionContext context = new ExecutionContext();
        AddCommand add_command = new AddCommand();

        context.GetStack().push(5.0);

        assertThrows(IllegalArgumentException.class, () -> add_command.Execute(context, new String[]{}));
    }
}
