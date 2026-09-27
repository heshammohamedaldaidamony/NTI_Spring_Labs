package org.nti.lab1_task_tracker;
import org.nti.lab1_task_tracker.models.Task;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TaskStore {

    private final ConcurrentHashMap<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idGenerator.incrementAndGet());
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public boolean deleteById(Long id) {
        if(tasks.containsKey(id)) {
            tasks.remove(id);
            return true;
        }
        return false;
    }

    public int size() {
        return tasks.size();
    }
}
