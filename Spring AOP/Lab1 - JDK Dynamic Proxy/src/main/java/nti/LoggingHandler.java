package nti;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class LoggingHandler implements InvocationHandler {

    private final Object target;

    public LoggingHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        String argsString = (args == null) ? "[]" : Arrays.toString(args);

        System.out.println(">>> BEFORE: " + methodName + " args=" + argsString);

        long start = System.nanoTime();

        Object result;
        result = method.invoke(target, args);

        long elapsedNs = System.nanoTime() - start;
        double elapsedMs = elapsedNs / 1_000_000.0;

        System.out.println(">>> AFTER : " + methodName + " returned=" + result
                + " (took " + elapsedMs + " ms)");

        return result;
    }
}
