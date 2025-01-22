package commands;

import org.junit.jupiter.api.Test;
import task2.ExecutionContext;
import task2.commands.PrintCommand;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
