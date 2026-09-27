package org.nti.lab1_task_tracker.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.nti.lab1_task_tracker.TaskStore;
import org.nti.lab1_task_tracker.models.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskStore store;
    private final int maxTasks;
    private final int defaultPageSize;

    public TaskController(TaskStore store, @Value("${tasktracker.max-tasks:10}") int maxTasks,
                          @Value("${tasktracker.default-page-size}") int defaultPageSize) {
        this.store = store;
        this.maxTasks = maxTasks;
        this.defaultPageSize = defaultPageSize;
    }

    //--------------------- Add A Task---------------------
    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody Task task) {
        if (store.size() >= maxTasks) {
            return ResponseEntity.status(409).build();
        }
        task.setId(null);
        Task saved = store.save(task);
        return ResponseEntity
                .created(java.net.URI.create("/api/tasks/" + saved.getId()))
                .body(saved);
    }

    //---------------- Get All Tasks, Filters(limit,completed) are optional  --------
    @GetMapping
    public List<Task> list(
            @RequestParam(name = "completed", required = false) boolean completed,
            @RequestParam(name = "limit", required = false) Integer limit) {

        int effectiveLimit=defaultPageSize;
        if (limit !=null && limit > 0) {
            effectiveLimit=limit;;
        }

//        List<Task> result = new ArrayList<>();
//        for (Task t : store.findAll()) {
//            if (completed == null) {
//                result.add(t);
//            } else if (t.isCompleted() == completed) {
//                result.add(t);
//            }
//
        return store.findAll().stream()
                .filter(t -> t.isCompleted() == completed)
                .limit(effectiveLimit)
                .toList();
    }


    //------------ Get One Task By Id------------
    @GetMapping("/{id}")
    public ResponseEntity<Task> getOne(@PathVariable Long id) {
        Optional<Task> found = store.findById(id);
        if (found.isPresent()) {
            return ResponseEntity.ok(found.get());
        }
        return ResponseEntity.notFound().build();
    }


    //---------- Update A Task -------------
    @PutMapping("/{id}")
    public ResponseEntity<Task> replace(
            @PathVariable Long id,
            @Valid @RequestBody Task updated) {

        Optional<Task> existing = store.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Task task = existing.get();
        task.setTitle(updated.getTitle());
        task.setDescription(updated.getDescription());
        task.setCompleted(updated.isCompleted());
        task.setDueDate(updated.getDueDate());

        store.save(task);
        return ResponseEntity.ok(task);
    }

    //--------------- Patch A Task ---------------
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Task> markComplete(@PathVariable Long id) {
        Optional<Task> existing = store.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Task task = existing.get();
        task.setCompleted(true);
        store.save(task);
        return ResponseEntity.ok(task);
    }

    //----------- Delete A Task -------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (store.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
