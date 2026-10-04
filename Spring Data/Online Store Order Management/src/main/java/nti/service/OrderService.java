package nti.service;

import nti.dto.OrderSummary;
import nti.exception.InsufficientStockException;
import nti.exception.InvalidOrderStateException;
import nti.model.Customer;
import nti.model.Order;
import nti.model.OrderItem;
import nti.model.OrderStatus;
import nti.model.Payment;
import nti.model.PaymentMethod;
import nti.model.Product;
import nti.repository.CustomerRepository;
import nti.repository.OrderRepository;
import nti.repository.PaymentRepository;
import nti.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;

    public OrderService(OrderRepository orderRepository,
                        PaymentRepository paymentRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository,
                        AuditLogService auditLogService) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Order placeOrder(Long customerId, Map<Long, Integer> productQuantities) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("No customer with id " + customerId));

        Order order = new Order(customer);

        for (Map.Entry<Long, Integer> e : productQuantities.entrySet()) {
            Long productId = e.getKey();
            Integer qty = e.getValue();

            if (qty == null || qty <= 0) {
                throw new IllegalArgumentException("quantity must be > 0 for product " + productId);
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("No product with id " + productId));

            if (product.getStock() < qty) {
                throw new InsufficientStockException(productId, qty, product.getStock());
            }

            product.decreaseStock(qty);
            order.addItem(product, qty);
        }

        Order saved = orderRepository.save(order);
        auditLogService.record("placeOrder order=" + saved.getId());
        return saved;
    }

    @Transactional
    public Order pay(Long orderId, PaymentMethod method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No order with id " + orderId));

        if (order.getStatus() != OrderStatus.NEW) {
            throw new InvalidOrderStateException(orderId, order.getStatus(), "pay");
        }

        Payment payment = new Payment(order, order.getTotal(), method);
        paymentRepository.save(payment);
        order.setPayment(payment);
        order.setStatus(OrderStatus.PAID);
        return order;
    }

    @Transactional
    public Order ship(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No order with id " + orderId));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOrderStateException(orderId, order.getStatus(), "ship");
        }

        order.setStatus(OrderStatus.SHIPPED);
        return order;
    }

    @Transactional
    public Order cancel(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No order with id " + orderId));

        if (order.getStatus() != OrderStatus.NEW && order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOrderStateException(orderId, order.getStatus(), "cancel");
        }

        for (OrderItem item : order.getItems()) {
            Product p = item.getProduct();
            p.setStock(p.getStock() + item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return order;
    }

    @Transactional(readOnly = true)
    public OrderSummary getOrderSummary(Long orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No order with id " + orderId));

        List<OrderSummary.Line> lines = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            lines.add(new OrderSummary.Line(
                    item.getProduct().getSku(),
                    item.getQuantity(),
                    item.getUnitPrice()
            ));
        }

        return new OrderSummary(
                order.getId(),
                order.getCustomer().getName(),
                order.getStatus(),
                order.getTotal(),
                lines
        );
    }
}