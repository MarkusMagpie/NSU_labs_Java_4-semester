package task2.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import task2.ExecutionContext;

import java.util.EmptyStackException;

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
