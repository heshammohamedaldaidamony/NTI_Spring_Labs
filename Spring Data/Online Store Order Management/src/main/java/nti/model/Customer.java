package nti.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer",
        uniqueConstraints = @UniqueConstraint(name = "uk_customer_email", columnNames = "email"))
public class Customer extends BaseEntity {

    private String name;

    @Column(nullable = false)
    private String email;

    @Embedded
    private Address shippingAddress;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private Set<Order> orders = new HashSet<>();

    protected Customer() {}

    public Customer(String name, String email, Address shippingAddress) {
        this.name = name;
        this.email = email;
        this.shippingAddress = shippingAddress;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public Address getShippingAddress() { return shippingAddress; }
    public Set<Order> getOrders() { return orders; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }

    @Override
    public String toString() {
        return "Customer{id=" + getId() + ", name='" + name + "', email='" + email
                + "', address=" + shippingAddress + "}";
    }
}