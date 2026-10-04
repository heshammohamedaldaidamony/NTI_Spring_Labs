package nti.service;

import nti.model.Product;
import nti.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product restock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0, was " + quantity);
        }
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("No product with id " + productId));
        p.setStock(p.getStock() + quantity);
        return p;
    }

    @Transactional
    public Product changePrice(Long productId, BigDecimal newPrice) {
        if (newPrice == null || newPrice.signum() < 0) {
            throw new IllegalArgumentException("newPrice must be >= 0, was " + newPrice);
        }
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("No product with id " + productId));
        p.setPrice(newPrice);
        return p;
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Transactional(readOnly = true)
    public List<Product> findByCategory(String categoryName) {
        return productRepository.findByCategory(categoryName);
    }

    @Transactional(readOnly = true)
    public List<Product> findLowStock(int threshold) {
        return productRepository.findLowStock(threshold);
    }

    @Transactional(readOnly = true)
    public List<Product> findPage(int page, int size) {
        return productRepository.findPage(page, size);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return productRepository.countAll();
    }
}