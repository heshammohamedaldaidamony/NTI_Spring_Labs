package nti.service;

import nti.model.Product;
import nti.repository.ProductRepository;
import nti.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ProductRepository productRepository;
    private final ReportRepository reportRepository;

    public ReportService(ProductRepository productRepository,
                         ReportRepository reportRepository) {
        this.productRepository = productRepository;
        this.reportRepository = reportRepository;
    }

    @Transactional
    public void demonstrateDiscountAndClear(Long productId, String category, double percent) {
        Product before = productRepository.findById(productId).orElseThrow();
        System.out.println("  In-memory price before applyDiscount = " + before.getPrice());

        reportRepository.applyDiscount(category, percent);
        System.out.println("  After applyDiscount (before re-read)     = " + before.getPrice());

        Product after = productRepository.findById(productId).orElseThrow();
        System.out.println("  After em.clear() + findById              = " + after.getPrice());
    }
}