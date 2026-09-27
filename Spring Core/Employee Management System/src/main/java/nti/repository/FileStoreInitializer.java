package nti.repository;

import org.springframework.stereotype.Component;

@Component
public class FileStoreInitializer {

    public FileStoreInitializer() {
        System.out.println("[FileStoreInitializer] preparing file store (runs first)");
    }
}