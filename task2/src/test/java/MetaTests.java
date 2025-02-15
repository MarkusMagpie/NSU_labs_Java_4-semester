import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import task2.ExecutionContext;
import task2.commands.*;

import java.util.EmptyStackException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class MetaTests {
    private ExecutionContext context;
    private DefineCommand define_command;
    private PushCommand push_command;

    @BeforeEach
    void setUp() {
        context = new ExecutionContext();
        define_command = new DefineCommand();
        push_command = new PushCommand();
    }

    @Test
    void Test1() {
        define_command.Execute(context, new String[]{"x", "4.0"});
        assertEquals(4.0, context.GetVariables().get("x"));

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

    @Test
    void Test2_NegativeSqrt() {
        define_command.Execute(context, new String[]{"x", "-4.0"});
        assertEquals(-4.0, context.GetVariables().get("x"));

        push_command.Execute(context, new String[]{"x"});
        assertEquals(1, context.GetStack().size());

        SqrtCommand sqrt_command = new SqrtCommand();
        assertThrows(ArithmeticException.class, () -> sqrt_command.Execute(context, new String[]{}));
    }

    @Test
    void Test3_SqrtThrows() {
        SqrtCommand sqrt_command = new SqrtCommand();
        assertThrows(EmptyStackException.class, () -> sqrt_command.Execute(context, new String[]{}));
    }

    @Test
    void Test4_AddThrows() {
        define_command.Execute(context, new String[]{"x", "10.0"});
        assertEquals(10.0, context.GetVariables().get("x"));

        push_command.Execute(context, new String[]{"x"});
        assertEquals(1, context.GetStack().size());

        AddCommand add_command = new AddCommand();
        assertThrows(IllegalArgumentException.class, () -> add_command.Execute(context, new String[]{}));
    }

    @Test
    void Test5_SubThrows() {
        define_command.Execute(context, new String[]{"x", "10.0"});
        assertEquals(10.0, context.GetVariables().get("x"));

        push_command.Execute(context, new String[]{"x"});
        assertEquals(1, context.GetStack().size());

        AddCommand sub_command = new AddCommand();
        assertThrows(IllegalArgumentException.class, () -> sub_command.Execute(context, new String[]{}));
    }

    @Test
    void Test6_MulThrows() {
        define_command.Execute(context, new String[]{"x", "10.0"});
        define_command.Execute(context, new String[]{"y", "5.0"});
        assertEquals(10.0, context.GetVariables().get("x"));
        assertEquals(5.0, context.GetVariables().get("y"));

        push_command.Execute(context, new String[]{"x"});
        assertEquals(1, context.GetStack().size());
        push_command.Execute(context, new String[]{"y"});
        assertEquals(2, context.GetStack().size());

        AddCommand addCommand = new AddCommand();
        addCommand.Execute(context, new String[]{});
        assertEquals(1, context.GetStack().size());
        assertEquals(15.0, context.GetStack().peek()); // 10 + 5 = 15

        MulCommand mulCommand = new MulCommand();
        assertThrows(IllegalArgumentException.class, () -> mulCommand.Execute(context, new String[]{}));
        // у нас только 1 значение в стеке
    }

    @Test
    void Test7_DivThrows() {
        define_command.Execute(context, new String[]{"x", "20.0"});
        define_command.Execute(context, new String[]{"y", "5.0"});
        assertEquals(20.0, context.GetVariables().get("x"));
        assertEquals(5.0, context.GetVariables().get("y"));

        push_command.Execute(context, new String[]{"x"});
        assertEquals(1, context.GetStack().size());
        push_command.Execute(context, new String[]{"y"});
        assertEquals(2, context.GetStack().size());

        PopCommand pop_command = new PopCommand();
        pop_command.Execute(context, new String[]{});
        assertEquals(1, context.GetStack().size());
        assertEquals(20.0, context.GetStack().peek());

        DivCommand div_command = new DivCommand();
        assertThrows(IllegalArgumentException.class, () -> div_command.Execute(context, new String[]{}));
        // у нас только 1 значение в стеке
    }

    @Test
    void Test8_EmptyStack() {
        AddCommand add_command = new AddCommand();
        assertThrows(IllegalArgumentException.class, () -> add_command.Execute(context, new String[]{}));
//        IllegalArgumentException exception =
//                org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
//                        () -> add_command.Execute(context, new String[]{}));
//        assertEquals("Команда ADD принимает два аргумента. Аргументов не хватает.", exception.getMessage());


        PopCommand pop_command = new PopCommand();
        assertThrows(EmptyStackException.class, () -> pop_command.Execute(context, new String[]{}));

        PrintCommand print_command = new PrintCommand();
        assertThrows(EmptyStackException.class, () -> print_command.Execute(context, new String[]{}));
    }

    @Test
    void Test9_DivisionByZero() {
        define_command.Execute(context, new String[]{"a", "100.0"});
        assertEquals(100.0, context.GetVariables().get("a"));
        define_command.Execute(context, new String[]{"b", "0.0"});
        assertEquals(0.0, context.GetVariables().get("b"));

        push_command.Execute(context, new String[]{"a"});
        assertEquals(1, context.GetStack().size());
        push_command.Execute(context, new String[]{"b"});
        assertEquals(2, context.GetStack().size());

        DivCommand divCommand = new DivCommand();
        assertThrows(ArithmeticException.class, () -> divCommand.Execute(context, new String[]{}));
    }

    @Test
    void Test10_DefineThrows() {
        // define требует 2 аргумента
        assertThrows(IllegalArgumentException.class, () -> define_command.Execute(context, new String[]{"a"}));

        define_command.Execute(context, new String[]{"a", "100.0"});
        push_command.Execute(context, new String[]{"a"});
        assertThrows(IllegalArgumentException.class, () -> define_command.Execute(context, new String[]{"a"}));
    }

    // New tests after comments

    @Test
    void Test11_DefineThrows2() {
        assertThrows(IllegalArgumentException.class, () -> define_command.Execute(context, new String[]{"123", "100.0"}));
    }

    @Test
    void Test12_DivisionByZero2() {
        define_command.Execute(context, new String[]{"a", "100.0"});
        define_command.Execute(context, new String[]{"b", "0.0"});

        push_command.Execute(context, new String[]{"a"});
        assertEquals(1, context.GetStack().size());
        push_command.Execute(context, new String[]{"b"});
        assertEquals(2, context.GetStack().size());

        DivCommand divCommand = new DivCommand();
        assertThrows(ArithmeticException.class, () -> divCommand.Execute(context, new String[]{}));

        // проверяем дополнительно стек
        assertEquals(0.0, context.GetStack().peek());
    }

    @Test
    void Test13_NegativeSqrt2() {
        define_command.Execute(context, new String[]{"x", "-4.0"});
        push_command.Execute(context, new String[]{"x"});

        SqrtCommand sqrt_command = new SqrtCommand();
        assertThrows(ArithmeticException.class, () -> sqrt_command.Execute(context, new String[]{}));

        // проверяем дополнительно стек
        assertEquals(-4.0, context.GetStack().peek());
    }

    @Test
    void Test14_AddThrows2() {
        // проверка на MAX_VALUE
        define_command.Execute(context, new String[]{"x", String.valueOf(Double.MAX_VALUE)});
        define_command.Execute(context, new String[]{"y", "1.0"});

        push_command.Execute(context, new String[]{"x"});
        push_command.Execute(context, new String[]{"y"});

        AddCommand add_command = new AddCommand();
        assertThrows(ArithmeticException.class, () -> add_command.Execute(context, new String[]{}));

        // проверка на MIN_VALUE
        define_command.Execute(context, new String[]{"x", String.valueOf(Double.MIN_VALUE)});
        define_command.Execute(context, new String[]{"y", "0.0"});

        push_command.Execute(context, new String[]{"x"});
        push_command.Execute(context, new String[]{"y"});

        assertThrows(ArithmeticException.class, () -> add_command.Execute(context, new String[]{}));
    }

    @Test
    void Test15_DefineThrows2() {
        // проверки на inf, nan
        assertThrows(ArithmeticException.class, () -> define_command.Execute(context, new String[]{"a", String.valueOf(Double.NEGATIVE_INFINITY)}));
        assertThrows(ArithmeticException.class, () -> define_command.Execute(context, new String[]{"a", String.valueOf(Double.POSITIVE_INFINITY)}));
        assertThrows(ArithmeticException.class, () -> define_command.Execute(context, new String[]{"a", String.valueOf(Double.NaN)}));
    }

    @Test
    void Test16_DivThrows2() {
        // проверка на inf
        define_command.Execute(context, new String[]{"x", String.valueOf(1.0)});
        define_command.Execute(context, new String[]{"y", String.valueOf(0.0)});

        push_command.Execute(context, new String[]{"x"});
        push_command.Execute(context, new String[]{"y"});

        DivCommand div_command = new DivCommand();
        assertThrows(ArithmeticException.class, () -> div_command.Execute(context, new String[]{}));

        assertEquals(0.0, context.GetStack().peek());

        // проверка на nan
        define_command.Execute(context, new String[]{"x", String.valueOf(0.0)});
        define_command.Execute(context, new String[]{"y", String.valueOf(0.0)});

        push_command.Execute(context, new String[]{"x"});
        push_command.Execute(context, new String[]{"y"});

        assertThrows(ArithmeticException.class, () -> div_command.Execute(context, new String[]{}));

        assertEquals(0.0, context.GetStack().peek());
    }

    @Test
    void Test17_MulThrows2() {
        // проверка на MAX_VALUE
        define_command.Execute(context, new String[]{"x", String.valueOf(Double.MAX_VALUE)});
        define_command.Execute(context, new String[]{"y", String.valueOf(1.0)});

        push_command.Execute(context, new String[]{"x"});
        push_command.Execute(context, new String[]{"y"});

        MulCommand mul_command = new MulCommand();
        assertThrows(ArithmeticException.class, () -> mul_command.Execute(context, new String[]{}));

        assertEquals(1.0, context.GetStack().peek());

        // проверка на MIN_VALUE
        define_command.Execute(context, new String[]{"x", String.valueOf(Double.MIN_VALUE)});
        define_command.Execute(context, new String[]{"y", String.valueOf(1.0)});

        push_command.Execute(context, new String[]{"x"});
        push_command.Execute(context, new String[]{"y"});

        assertThrows(ArithmeticException.class, () -> mul_command.Execute(context, new String[]{}));

        assertEquals(1.0, context.GetStack().peek());
    }

    @Test
    void Test18_PushThrows() {
        assertThrows(NoSuchElementException.class, () -> push_command.Execute(context, new String[]{"x"}));
    }

    @Test
    void Test19_SqrtThrows2() {
        // проверка на MAX_VALUE
        define_command.Execute(context, new String[]{"x", String.valueOf(Double.MAX_VALUE)});
        push_command.Execute(context, new String[]{"x"});

        SqrtCommand sqrt_command = new SqrtCommand();
        assertThrows(ArithmeticException.class, () -> sqrt_command.Execute(context, new String[]{}));


        // проверка на MIN_VALUE
        define_command.Execute(context, new String[]{"y", String.valueOf(Double.MIN_VALUE)});
        push_command.Execute(context, new String[]{"y"});

        assertThrows(ArithmeticException.class, () -> sqrt_command.Execute(context, new String[]{}));
    }

    @Test
    void Test20_DivThrows2() {
        // проверка на MAX_VALUE
        define_command.Execute(context, new String[]{"x", String.valueOf(Double.MAX_VALUE)});
        define_command.Execute(context, new String[]{"y", String.valueOf(0.0)});

        push_command.Execute(context, new String[]{"x"});
        push_command.Execute(context, new String[]{"y"});

        SubCommand sub_command = new SubCommand();
        assertThrows(ArithmeticException.class, () -> sub_command.Execute(context, new String[]{}));

        assertEquals(0.0, context.GetStack().peek());

        // проверка на MIN_VALUE
        define_command.Execute(context, new String[]{"x", String.valueOf(Double.MIN_VALUE)});
        define_command.Execute(context, new String[]{"y", String.valueOf(1.0)});

        push_command.Execute(context, new String[]{"x"});
        push_command.Execute(context, new String[]{"y"});

        assertThrows(ArithmeticException.class, () -> sub_command.Execute(context, new String[]{}));

        assertEquals(1.0, context.GetStack().peek());
    }
}
