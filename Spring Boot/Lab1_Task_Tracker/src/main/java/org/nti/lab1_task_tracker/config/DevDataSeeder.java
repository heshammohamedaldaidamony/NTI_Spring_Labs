package org.nti.lab1_task_tracker.config;

import org.nti.lab1_task_tracker.TaskStore;
import org.nti.lab1_task_tracker.models.Task;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@Configuration
@Profile("dev")
public class DevDataSeeder {

    @Bean
    CommandLineRunner seedTasks(TaskStore store) {
        return args -> {
            Task t1 = new Task();
            t1.setTitle("Sample task A");
            t1.setDueDate(LocalDate.now().plusDays(1));
            store.save(t1);

            Task t2 = new Task();
            t2.setTitle("Sample task B");
            store.save(t2);

            System.out.println("Seeded " + store.size() + " tasks for dev profile");
        };
    }
}
