package nti.audit;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class LazyBean {

    public LazyBean() {
        System.out.println("[LazyBean] constructed (this should NOT print until first use)");
    }

    public void ping() {
        System.out.println("[LazyBean] ping");
    }
}