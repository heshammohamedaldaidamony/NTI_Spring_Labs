package nti.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CUST")
public class Customer extends Person {

    private String email;

    protected Customer() {}

    public Customer(String name, String email) {
        super(name);
        this.email = email;
    }

    public String getEmail() { return email; }
}