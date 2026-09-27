package nti.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnProdProfileCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String[] active = context.getEnvironment().getActiveProfiles();
        for (String p : active) {
            if ("prod".equalsIgnoreCase(p)) return true;
        }
        return false;
    }
}