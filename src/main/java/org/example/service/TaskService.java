package org.example.service;

import org.example.model.activity.ActivityType;
import org.example.model.task.*;
import org.example.model.user.User;
import org.example.repository.ActivityRepository;
import org.example.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TaskService {
    private final TaskRepository taskRepository;
    private final ActivityRepository activityRepository;

    public TaskService(TaskRepository taskRepository , ActivityRepository activityRepository){
        this.taskRepository = taskRepository;
        this.activityRepository = activityRepository;
    }

    public Task createTask(String title , String description , User creator){
        Task task = new TaskBuilder(title , description , creator).build();

        taskRepository.save(task);
        ActivityEvent event = new ActivityEvent(task.getId(),
                ActivityType.TASK_CREATED ,
                creator ,
                String.format("Task created : %s" , title));
        activityRepository.save(event);

        return task;
    }

    public Optional<Task> viewTask(String taskId){
        return taskRepository.findById(taskId);
    }

    public Task updateStatus(String taskId , TaskStatus newStatus , User performedBy){
        Task currTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found :" + taskId));

        if(!currTask.getStatus().isValidTransition(newStatus)){
            throw new RuntimeException("Invalid Status Transition.");
        }

        Task updatedTask = Task.from(currTask)
                .status(newStatus)
                .build();

        taskRepository.save(updatedTask);

        ActivityEvent event = new ActivityEvent(taskId , ActivityType.STATUS_CHANGED ,
                performedBy , String.format("%s -> %s" , currTask.getStatus() , newStatus));
        activityRepository.save(event);

        return updatedTask;
    }

    public Task assignTask(String taskId , User assignee , User performedBy){
        Task currTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found :" + taskId));

        Task updatedTask = Task.from(currTask)
                .assignTo(assignee)
                .build();

        taskRepository.save(updatedTask);

        String previousAssignee = currTask.getAssignedTo() != null ?
                currTask.getAssignedTo().getEmail() : "Unassigned";

        String newAssignee = assignee != null ? assignee.getEmail() : "Unassigned";

        ActivityEvent event = new ActivityEvent(taskId , ActivityType.ASSIGNEE_CHANGED ,
                performedBy , String.format("%s -> %s" , previousAssignee , newAssignee));
        activityRepository.save(event);

        return updatedTask;
    }

    public Task unassignTask(String taskId , User performedBy){
        return assignTask(taskId , null , performedBy);
    }

    public Task updatePriority(String taskId , Priority newPriority , User performedBy){
        Task currTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found :" + taskId));

        Task updatedTask = Task.from(currTask)
                .priority(newPriority)
                .build();

        taskRepository.save(updatedTask);

        ActivityEvent event = new ActivityEvent(taskId , ActivityType.PRIORITY_CHANGED ,
                performedBy , String.format("%s -> %s" , currTask.getPriority() , newPriority));
        activityRepository.save(event);

        return updatedTask;
    }

    public Task updateDueDate(String taskId , LocalDate dueDate, User performedBy){
        Task currTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found :" + taskId));

        Task updatedTask = Task.from(currTask)
                .dueDate(dueDate)
                .build();

        taskRepository.save(updatedTask);

        String previousDate = currTask.getDueDate() != null ?
                currTask.getDueDate().toString() : "No Deadline";
        String newDate = dueDate != null ? dueDate.toString() : "No Deadline";

        ActivityEvent event = new ActivityEvent(taskId , ActivityType.DUE_DATE_CHANGED ,
                performedBy , String.format("%s -> %s" , previousDate , newDate));
        activityRepository.save(event);

        return updatedTask;
    }

    public Task addTag(String taskId , String tag , User performedBy){
        Task currTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found :" + taskId));


        Task updatedTask = Task.from(currTask)
                .addTag(tag)
                .build();

        taskRepository.save(updatedTask);

        ActivityEvent event = new ActivityEvent(taskId , ActivityType.TAG_ADDED,
                performedBy , String.format("Added tag : %s" , tag));
        activityRepository.save(event);

        return updatedTask;
    }

    public Task addComment(String taskId , String text , User author){
        Task currTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found :" + taskId));

        Comment comment = new Comment(author , text);
        Task updatedTask = Task.from(currTask)
                .addComment(comment)
                .build();

        taskRepository.save(updatedTask);

        ActivityEvent event = new ActivityEvent(taskId , ActivityType.COMMENT_ADDED,
                author , String.format("Comment by : %s" , author.getEmail()));
        activityRepository.save(event);

        return updatedTask;
    }

    public List<Task> viewTaskHistory(String taskId){
        return taskRepository.findAllVersions(taskId);
    }

    public List<ActivityEvent> getActivityLog(String taskId){
        return activityRepository.findByTaskId(taskId);
    }

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }
}
