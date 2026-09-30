package nti.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.Arrays;

public class TimingAroundAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArguments();
        Object target = invocation.getThis();

        String methodName = method.getName();
        String argsString = (args == null || args.length == 0) ? "[]" : Arrays.toString(args);
        String targetName = (target == null) ? "?" : target.getClass().getSimpleName();

        System.out.println("### TIMER start: " + targetName + "." + methodName
                + " args=" + argsString);

        long start = System.nanoTime();
        try {
            return invocation.proceed();
        } finally {
            double ms = (System.nanoTime() - start) / 1_000_000.0;
            System.out.println("### TIMER end  : " + targetName + "." + methodName
                    + " (took " + ms + " ms)");
        }
    }
}