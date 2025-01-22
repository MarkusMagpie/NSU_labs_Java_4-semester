package task2.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import task2.ExecutionContext;

import java.util.EmptyStackException;

public class PrintCommandTest {
    @Test
    void testPrint() {
        ExecutionContext context = new ExecutionContext();
        PrintCommand printCommand = new PrintCommand();

        context.GetStack().push(42.0);

        // команда просто выведет значение, но не снимет его со стека.
        assertDoesNotThrow(() -> printCommand.Execute(context, new String[]{}));
        assertEquals(42.0, context.GetStack().peek());
    }

    @Test
    void testPrintEmptyStack() {
        ExecutionContext context = new ExecutionContext();
        PrintCommand printCommand = new PrintCommand();

        assertThrows(EmptyStackException.class, () -> printCommand.Execute(context, new String[]{}));
    }
}
