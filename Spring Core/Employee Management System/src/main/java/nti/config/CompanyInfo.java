package nti.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CompanyInfo implements InitializingBean, DisposableBean {

    private String name;
    private String currency;
    private int retryCount;
    private int maxRaisePercentage;

    @Value("${company.name}")
    public void setName(String name) { this.name = name; }

    @Value("${company.currency}")
    public void setCurrency(String currency) { this.currency = currency; }

    @Value("${notification.retry-count}")
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    @Value("${raise.max-percentage}")
    public void setMaxRaisePercentage(int maxRaisePercentage) {
        this.maxRaisePercentage = maxRaisePercentage;
    }

    public String getName() { return name; }
    public String getCurrency() { return currency; }
    public int getRetryCount() { return retryCount; }
    public int getMaxRaisePercentage() { return maxRaisePercentage; }

    @Override
    public String toString() {
        return "CompanyInfo{name='" + name + "', currency='" + currency
                + "', retryCount=" + retryCount
                + ", maxRaisePercentage=" + maxRaisePercentage + "}";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("[CompanyInfo] InitializingBean.afterPropertiesSet()");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("[CompanyInfo] DisposableBean.destroy()");
    }
}