package nti.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


public class OnDevProfileCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String[] active = context.getEnvironment().getActiveProfiles();
        for (String p : active) {
            if ("dev".equalsIgnoreCase(p)) return true;
        }
        return false;
    }
}