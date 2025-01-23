package commands;

import org.junit.jupiter.api.Test;

import task2.ExecutionContext;
import task2.commands.AddCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testAddThrows() {
        ExecutionContext context = new ExecutionContext();
        AddCommand add_command = new AddCommand();

        context.GetStack().push(5.0);

        assertThrows(IllegalArgumentException.class, () -> add_command.Execute(context, new String[]{}));
    }
}
