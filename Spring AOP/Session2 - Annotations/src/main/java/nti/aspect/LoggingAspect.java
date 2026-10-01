package nti.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* nti.service.OrderService.*(..))")
    public void orderServiceMethods() {}

    @Before("orderServiceMethods()")
    public void beforeMethod() {
        System.out.println("[BEFORE] Before method");
    }

    @AfterReturning(
            pointcut = "orderServiceMethods()",
            returning = "result"
    )
    public void afterReturning(Object result) {
        System.out.println("[AFTER RETURNING] Method returned: " + result);
    }

    @AfterThrowing(
            pointcut = "orderServiceMethods()",
            throwing = "exception"
    )
    public void afterThrowing(Exception exception) {
        System.out.println(
                "[AFTER THROWING] Method failed: " + exception.getMessage()
        );
    }

    @After("orderServiceMethods()")
    public void afterMethod() {
        System.out.println("[AFTER] Method finished");
    }

    @Order(1)
    @Around("within(nti.service.OrderService)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint)
            throws Throwable {

        System.out.println("Around - Before method");

        Object result = joinPoint.proceed();

        System.out.println("Around - After method");

        return result;
    }

    @Order(2)
    @Around("@annotation(nti.utility.Cacheable)")
    public Object aroundMethodCashing(ProceedingJoinPoint joinPoint)
            throws Throwable {

        System.out.println("Cashing - Before method");

        Object result = joinPoint.proceed();

        System.out.println("Cashing - After method");

        return result;
    }
}