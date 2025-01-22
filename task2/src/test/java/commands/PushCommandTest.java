package commands;

import org.junit.jupiter.api.Test;

import task2.ExecutionContext;
import task2.commands.PushCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PushCommandTest {
    @Test
    void testPushVariable() {
        ExecutionContext context = new ExecutionContext();
        PushCommand pushCommand = new PushCommand();

        context.GetVariables().put("x", 10.0);

        pushCommand.Execute(context, new String[]{"x"});

        assertEquals(1, context.GetStack().size());
        assertEquals(10.0, context.GetStack().peek());
    }

    @Test
    void testPushInvalidArgs() {
        ExecutionContext context = new ExecutionContext();
        PushCommand pushCommand = new PushCommand();

        assertThrows(IllegalArgumentException.class, () -> pushCommand.Execute(context, new String[]{}));
    }
}
