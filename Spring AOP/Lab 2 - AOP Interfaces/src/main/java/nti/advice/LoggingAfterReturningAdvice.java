package nti.advice;

import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;
import java.util.Arrays;

public class LoggingAfterReturningAdvice implements AfterReturningAdvice {

    @Override
    public void afterReturning(Object returnValue, Method method,
                               Object[] args, Object target) throws Throwable {
        if (!"checkStock".equals(method.getName())) {
            return;
        }

        String methodName = method.getName();
        String argsString = (args == null || args.length == 0)
                ? "[]"
                : Arrays.toString(args);

        System.out.println("<<< AFTER : checkStock returned=" + returnValue);

        System.out.println("<<< AFTER : " + target.getClass().getSimpleName()
                + "." + methodName + " args=" + argsString
                + " returned=" + returnValue);
    }
}