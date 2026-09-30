package nti.advice;

import org.springframework.aop.ThrowsAdvice;

import java.lang.reflect.Method;
import java.util.Arrays;

public class LoggingThrowsAdvice implements ThrowsAdvice {
    public void afterThrowing(Method method, Object[] args,
                              Object target, IllegalStateException ex) {

        if (!"reserveStock".equals(method.getName())) {
            return;
        }

        String methodName = method.getName();
        String argsString = (args == null || args.length == 0)
                ? "[]"
                : Arrays.toString(args);

        System.out.println("!!! THREW: " + target.getClass().getSimpleName()
                + "." + methodName + " args=" + argsString
                + " -> " + ex.getClass().getSimpleName()
                + ": " + ex.getMessage());
    }
}