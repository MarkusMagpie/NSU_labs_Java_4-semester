package commands;

import org.junit.jupiter.api.Test;

import task2.ExecutionContext;
import task2.commands.PopCommand;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PopCommandTest {
    @Test
    void testPopValue() {
        ExecutionContext context = new ExecutionContext();
        PopCommand popCommand = new PopCommand();

        context.GetStack().push(10.0);
        popCommand.Execute(context, new String[]{});

        assertEquals(0, context.GetStack().size());
    }

    @Test
    void testPopEmptyStack() {
        ExecutionContext context = new ExecutionContext();
        PopCommand popCommand = new PopCommand();

        assertThrows(EmptyStackException.class, () -> popCommand.Execute(context, new String[]{}));
    }
}
