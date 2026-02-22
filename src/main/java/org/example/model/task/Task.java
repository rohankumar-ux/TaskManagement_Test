package org.example.model.task;

import org.example.model.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Task {
    private final String id;
    private final int version;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final Priority priority;
    private final User createdBy;
    private final User assignedTo;
    private final LocalDate dueDate;
    private final List<String> tags;
    private final List<Comment> comments;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    Task(TaskBuilder builder){
        this.id = builder.id;
        this.version = builder.version;
        this.title = builder.title;
        this.description = builder.description;
        this.status = builder.status;
        this.priority = builder.priority;
        this.createdBy = builder.createdBy;
        this.assignedTo = builder.assignedTo;
        this.dueDate = builder.dueDate;
        this.tags = List.copyOf(builder.tags);
        this.comments = List.copyOf(builder.comments);
        this.createdAt = builder.createdAt;
        this.updatedAt = LocalDateTime.now();
    }

    public String getId(){
        return id;
    }
    public int getVersion(){
        return version;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public TaskStatus getStatus(){
        return status;
    }
    public Priority getPriority(){
        return priority;
    }
    public User getCreatedBy(){
        return createdBy;
    }
    public User getAssignedTo(){
        return assignedTo;
    }
    public LocalDate getDueDate(){
        return dueDate;
    }
    public List<String> getTags(){
        return tags;
    }
    public List<Comment> getComments(){
        return comments;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }

    public static TaskBuilder from(Task previous){
        return new TaskBuilder(previous);
    }
}
