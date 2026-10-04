package nti.dto;

import nti.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderSummary(
        Long orderId,
        String customerName,
        OrderStatus status,
        BigDecimal total,
        List<Line> lines
) {
    public record Line(String sku, int quantity, BigDecimal unitPrice) {}
}