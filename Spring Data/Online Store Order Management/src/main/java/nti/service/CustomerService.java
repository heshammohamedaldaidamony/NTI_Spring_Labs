package nti.service;

import nti.exception.DuplicateCustomerException;
import nti.model.Address;
import nti.model.Customer;
import nti.model.Order;
import nti.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public java.util.List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer register(String name, String email, Address address) {
        customerRepository.findByEmail(email).ifPresent(existing -> {
            throw new DuplicateCustomerException(email);
        });
        Customer c = new Customer(name, email, address);
        return customerRepository.save(c);
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No customer with id " + id));
    }

    @Transactional(readOnly = true)
    public Customer findByIdWithOrders(Long id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No customer with id " + id));
        for (Order o : c.getOrders()) {
            o.getItems().size();
        }
        return c;
    }
}