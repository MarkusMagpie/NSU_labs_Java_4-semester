package task2;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ExecutionContext {
    private final Stack<Double> stack = new Stack<>();
    private final Map<String, Double> variables = new HashMap<>();

    public Stack<Double> GetStack() {
        return stack;
    }

    public Map<String, Double> GetVariables() {
        return variables;
    }
}
