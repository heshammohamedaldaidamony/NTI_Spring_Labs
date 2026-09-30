package nti.advice;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.Arrays;

public class LoggingBeforeAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        String methodName = method.getName();
        String argsString = (args == null || args.length == 0)
                ? "[]"
                : Arrays.toString(args);

        System.out.println(">>> BEFORE: " + target.getClass().getSimpleName()
                + "." + methodName + " args=" + argsString);
    }
}