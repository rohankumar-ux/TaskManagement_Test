package org.example.model.task;

import org.example.model.activity.ActivityType;
import org.example.model.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class ActivityEvent {
    private final String eventId;
    private final String taskId;
    private final ActivityType activityType;
    private final User performedBy;
    private final LocalDateTime timestamp;
    private final String details;

    public ActivityEvent(String taskId , ActivityType activityType , User user , String details){
        this.eventId = UUID.randomUUID().toString();
        this.taskId = taskId;
        this.activityType = activityType;
        this.performedBy = user;
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTaskId() {
        return taskId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public String getDetails() {
        return details;
    }

    public User getPerformedBy() {
        return performedBy;
    }
}
