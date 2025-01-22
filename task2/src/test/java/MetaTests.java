import org.junit.jupiter.api.Test;

import task2.ExecutionContext;
import task2.commands.DefineCommand;
import task2.commands.PrintCommand;
import task2.commands.PushCommand;
import task2.commands.SqrtCommand;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetaTests {
    @Test
    void Test1() {
        ExecutionContext context = new ExecutionContext();

        DefineCommand define_command = new DefineCommand();
        define_command.Execute(context, new String[]{"x", "4.0"});
        assertEquals(4.0, context.GetVariables().get("x"));

        PushCommand push_command = new PushCommand();
        push_command.Execute(context, new String[]{"x"});
        assertEquals(1, context.GetStack().size());

        SqrtCommand sqrt_command = new SqrtCommand();
        sqrt_command.Execute(context, new String[]{});
        assertEquals(1, context.GetStack().size());
        assertEquals(2.0, context.GetStack().peek());

        PrintCommand print_command = new PrintCommand();
        print_command.Execute(context, new String[]{});
        assertEquals(2.0, context.GetStack().peek());
        assertDoesNotThrow(() -> print_command.Execute(context, new String[]{}));
    }
}
