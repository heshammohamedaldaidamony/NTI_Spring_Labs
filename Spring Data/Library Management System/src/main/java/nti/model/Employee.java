package nti.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("EMP")
public class Employee extends Person {

    private String department;

    protected Employee() {}
    public Employee(String name, String department) {
        super(name);
        this.department = department;
    }

    public String getDepartment() { return department; }
}