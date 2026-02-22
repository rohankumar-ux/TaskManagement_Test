package org.example.model.task;

import org.example.model.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskBuilder {
    String id;
    int version;
    String title;
    String description;
    User createdBy;

    TaskStatus status;
    Priority priority;
    User assignedTo;
    LocalDate dueDate;
    List<String> tags;
    List<Comment> comments;
    LocalDateTime createdAt;

    public TaskBuilder(String title , String description,User createdBy){
        this.id = UUID.randomUUID().toString();
        this.version = 1;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.status = TaskStatus.OPEN;
        this.priority = Priority.MEDIUM;
        tags = new ArrayList<>();
        comments = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public TaskBuilder(Task previous){
        this.id = previous.getId();
        this.version = previous.getVersion() + 1;
        this.title = previous.getTitle();
        this.description = previous.getDescription();
        this.createdBy = previous.getCreatedBy();
        this.status = previous.getStatus();
        this.priority = previous.getPriority();
        this.assignedTo = previous.getAssignedTo();
        this.dueDate = previous.getDueDate();
        this.tags = new ArrayList<>(previous.getTags());
        this.comments = new ArrayList<>(previous.getComments());
        this.createdAt = previous.getCreatedAt();
    }

    public TaskBuilder status(TaskStatus status){
        this.status = status;
        return this;
    }

    public TaskBuilder priority(Priority priority){
        this.priority = priority;
        return this;
    }

    public TaskBuilder assignTo(User user){
        this.assignedTo = user;
        return this;
    }

    public TaskBuilder dueDate(LocalDate date){
        this.dueDate = date;
        return this;
    }

    public TaskBuilder addTag(String tag){
        this.tags.add(tag);
        return this;
    }

    public TaskBuilder addComment(Comment comment){
        this.comments.add(comment);
        return this;
    }

    public Task build(){
        if(title == null || description == null || createdBy == null){
            throw new RuntimeException("Required fields missing");
        }
        return new Task(this);
    }

}
