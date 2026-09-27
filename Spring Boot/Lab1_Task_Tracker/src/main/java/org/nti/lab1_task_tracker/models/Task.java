package org.nti.lab1_task_tracker.models;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;

public class Task {
    private Long id;
    @NotBlank(message = "title is required")
    private String title;
    private String description;
    private boolean completed = false;
    private LocalDate dueDate;

    public Task() {}

    public Task(Long id, String title, String description, boolean completed, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.dueDate = dueDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
