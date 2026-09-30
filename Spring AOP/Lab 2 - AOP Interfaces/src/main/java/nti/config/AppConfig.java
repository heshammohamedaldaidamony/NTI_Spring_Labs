package nti.config;

import nti.advice.*;
import nti.service.InventoryService;
import nti.service.InventoryServiceImpl;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean public TimingAroundAdvice timingAroundAdvice()             { return new TimingAroundAdvice(); }
    @Bean public LoggingBeforeAdvice loggingBeforeAdvice()           { return new LoggingBeforeAdvice(); }
    @Bean public LoggingAfterReturningAdvice loggingAfterReturning() { return new LoggingAfterReturningAdvice(); }
    @Bean public LoggingThrowsAdvice loggingThrowsAdvice()           { return new LoggingThrowsAdvice(); }

    @Bean
    public ProxyFactoryBean inventoryServiceProxy(TimingAroundAdvice timing,
                                                  LoggingBeforeAdvice before,
                                                  LoggingAfterReturningAdvice after,
                                                  LoggingThrowsAdvice throwsAdvice) {

        ProxyFactoryBean pfb = new ProxyFactoryBean();
        pfb.setTarget(new InventoryServiceImpl());
        pfb.setInterfaces(InventoryService.class);

        pfb.addAdvice(timing);

        NameMatchMethodPointcut reserveOnly = new NameMatchMethodPointcut();
        reserveOnly.setMappedName("reserveStock");
        DefaultPointcutAdvisor beforeAdvisor =
                new DefaultPointcutAdvisor(reserveOnly, before);
        pfb.addAdvisor(beforeAdvisor);

        pfb.addAdvice(after);

        pfb.addAdvice(throwsAdvice);

        return pfb;
    }
}