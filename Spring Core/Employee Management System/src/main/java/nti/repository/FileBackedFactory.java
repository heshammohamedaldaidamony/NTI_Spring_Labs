package nti.repository;

public class FileBackedFactory {

    public static FileBackedEmployeeRepository create(String filePath) {
        return new FileBackedEmployeeRepository(filePath);
    }
}