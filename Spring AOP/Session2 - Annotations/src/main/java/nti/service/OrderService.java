package nti.service;

import nti.utility.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public String createOrder(int id) {
        if (id < 0) {
            throw new IllegalArgumentException();
        }
        System.out.println("[REAL TARGET] Creating order...");
        return "Order-"+id+" created";
    }

    @Cacheable
    public int getOrder(int id) {
        System.out.println("Getting order by id: ");
        return id;
    }
}