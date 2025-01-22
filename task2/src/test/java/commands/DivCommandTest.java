package commands;

import org.junit.jupiter.api.Test;
import task2.ExecutionContext;
import task2.commands.DivCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DivCommandTest {
    @Test
    void testDiv() {
        ExecutionContext context = new ExecutionContext();
        DivCommand div_command = new DivCommand();

        context.GetStack().push(6.0);
        context.GetStack().push(3.0);

        div_command.Execute(context, new String[]{});

        assertEquals(1, context.GetStack().size());
        assertEquals(2.0, context.GetStack().peek());
    }

    @Test
    void testDivideByZero() {
        ExecutionContext context = new ExecutionContext();
        DivCommand div_command = new DivCommand();

        context.GetStack().push(5.0);
        context.GetStack().push(0.0);

        assertThrows(ArithmeticException.class, () -> div_command.Execute(context, new String[]{}));
    }

    @Test
    void testDivInsufficientOperands() {
        ExecutionContext context = new ExecutionContext();
        DivCommand div_command = new DivCommand();

        context.GetStack().push(10.0);

        assertThrows(IllegalArgumentException.class, () -> div_command.Execute(context, new String[]{}));
    }
}
