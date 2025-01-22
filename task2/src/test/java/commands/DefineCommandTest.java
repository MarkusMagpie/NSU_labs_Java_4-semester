package commands;

import org.junit.jupiter.api.Test;
import task2.ExecutionContext;
import task2.commands.DefineCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefineCommandTest {
    @Test
    void testDefine() {
        ExecutionContext context = new ExecutionContext();
        DefineCommand define_command = new DefineCommand();

        define_command.Execute(context, new String[]{"x", "10"});

        assertEquals(10.0, context.GetVariables().get("x"));
    }

    @Test
    void testDefineInvalidArgs() {
        ExecutionContext context = new ExecutionContext();
        DefineCommand define_command = new DefineCommand();

        assertThrows(IllegalArgumentException.class, () -> define_command.Execute(context, new String[]{"x"}));
    }
}
