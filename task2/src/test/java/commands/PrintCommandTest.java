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
        PrintCommand print_command = new PrintCommand();

        context.GetStack().push(42.0);

        // print_command just prints the value, but doesn't remove it from the stack
        assertDoesNotThrow(() -> print_command.Execute(context, new String[]{}));
        assertEquals(42.0, context.GetStack().peek());
    }

    @Test
    void testPrintEmptyStack() {
        ExecutionContext context = new ExecutionContext();
        PrintCommand print_command = new PrintCommand();

        assertThrows(EmptyStackException.class, () -> print_command.Execute(context, new String[]{}));
    }
}
