package nti.repository;

import nti.model.Employee;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("prod")
@DependsOn("fileStoreInitializer")
public class FileBackedEmployeeRepository implements EmployeeRepository {

    private final Path file;
    private final List<Employee> cache = new ArrayList<>();

    public FileBackedEmployeeRepository(String filePath) {
        this.file = Paths.get(filePath);
    }

    @Override
    public void save(Employee employee) {
        cache.add(employee);
        persist();
    }

    @Override
    public Employee findById(int id) {
        return cache.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(cache);
    }

    private void persist() {
        try {
            List<String> lines = new ArrayList<>();
            for (Employee e : cache) {
                lines.add(e.getId() + "," + e.getName() + "," + e.getDepartment() + "," + e.getSalary());
            }
            Files.write(file, lines);
        } catch (IOException io) {
            throw new RuntimeException("Failed to persist employees to " + file, io);
        }
    }
}