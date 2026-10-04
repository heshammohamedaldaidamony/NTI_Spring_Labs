package nti.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "product",
        uniqueConstraints = @UniqueConstraint(name = "uk_product_sku", columnNames = "sku"))
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    protected Product() {}

    public Product(String sku, String name, BigDecimal price, int stock) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public void addCategory(Category c) {
        categories.add(c);
        c.getProducts().add(this);
    }

    public void removeCategory(Category c) {
        categories.remove(c);
        c.getProducts().remove(this);
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public Set<Category> getCategories() { return categories; }

    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product p)) return false;
        return Objects.equals(sku, p.sku);
    }

    @Override
    public int hashCode() { return Objects.hash(sku); }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0, was " + quantity);
        }
        if (quantity > stock) {
            throw new nti.exception.InsufficientStockException(getId(), quantity, stock);
        }
        this.stock -= quantity;
    }

    @Override
    public String toString() {
        return "Product{id=" + getId() + ", sku='" + sku + "', name='" + name
                + "', price=" + price + ", stock=" + stock
                + ", categories=" + categories + "}";
    }
}