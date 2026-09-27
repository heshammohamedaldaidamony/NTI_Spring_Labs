package nti.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class LifecycleLoggingPostProcessor implements BeanPostProcessor {

    private static final boolean ENABLED = Boolean.getBoolean("demo.bpp");

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (ENABLED) System.out.println("    [BPP-before] " + beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (ENABLED) System.out.println("    [BPP-after ] " + beanName);
        return bean;
    }
}