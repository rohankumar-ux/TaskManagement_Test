package org.example.service;

import org.example.model.task.Priority;
import org.example.model.task.Task;
import org.example.model.task.TaskStatus;
import org.example.model.user.User;
import org.example.repository.TaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TaskSearchService {
    private final TaskRepository taskRepository;

    public TaskSearchService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    public List<Task> filterByStatus(TaskStatus status){
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Task> filterByStatuses(TaskStatus... statuses){
        Set<TaskStatus> statusSet = new HashSet<>(Arrays.asList(statuses));

        return taskRepository.findAll().stream()
                .filter(task -> statusSet.contains(task.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Task> filterByPriority(Priority priority){
        return taskRepository.findAll().stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<Task> filterByPriorities(Priority... priorities){
        Set<Priority> prioritySet = new HashSet<>(Arrays.asList(priorities));

        return taskRepository.findAll().stream()
                .filter(task -> prioritySet.contains(task.getPriority()))
                .collect(Collectors.toList());
    }

    public List<Task> filterByAssignee(User user){
        return taskRepository.findAll().stream()
                .filter(task -> task.getAssignedTo() != null &&
                        task.getAssignedTo().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    public List<Task> filterUnassignee(User user){
        return taskRepository.findAll().stream()
                .filter(task -> task.getAssignedTo() == null)
                .collect(Collectors.toList());
    }

    public Map<User , List<Task>> groupByAssignee(){
        return taskRepository.findAll().stream()
                .filter(task -> task.getAssignedTo() != null)
                .collect(Collectors.groupingBy(Task::getAssignedTo));
    }

    public List<Task> filterByCreator(User creator){
        return taskRepository.findAll().stream()
                .filter(task -> task.getCreatedBy() != null &&
                        task.getCreatedBy().getId().equals(creator.getId()))
                .collect(Collectors.toList());
    }

    public List<Task> findCreatedBetween(LocalDateTime start , LocalDateTime end){
        return taskRepository.findAll().stream()
                .filter(task -> !task.getCreatedAt().isBefore(start) &&
                        !task.getCreatedAt().isAfter(end))
                .collect(Collectors.toList());
    }

    public List<Task> findCompletedBetween(LocalDateTime start , LocalDateTime end){
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .filter(task -> !task.getCreatedAt().isBefore(start) &&
                        !task.getCreatedAt().isAfter(end))
                .collect(Collectors.toList());
    }

    public List<Task> findModifiedBetween(LocalDateTime start , LocalDateTime end){
        return taskRepository.findAll().stream()
                .filter(task -> !task.getUpdatedAt().isBefore(start) &&
                        !task.getUpdatedAt().isAfter(end))
                .collect(Collectors.toList());
    }


    public List<Task> findByTag(String tag){
        return taskRepository.findAll().stream()
                .filter(task -> task.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    public List<Task> findByAnyTag(String... tags){
        Set<String> tagSet = new HashSet<>(Arrays.asList(tags));
        return taskRepository.findAll().stream()
                .filter(task -> task.getTags().stream().anyMatch(tagSet::contains))
                .collect(Collectors.toList());
    }

    public List<Task> findByAllTag(String... tags){
        Set<String> tagSet = new HashSet<>(Arrays.asList(tags));
        return taskRepository.findAll().stream()
                .filter(task -> new HashSet<>(task.getTags()).containsAll(tagSet))
                .collect(Collectors.toList());
    }

    public Map<Task , Long> findOverdueTasks(){
        LocalDate today = LocalDate.now();

        return taskRepository.findAll().stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> task.getStatus() != TaskStatus.COMPLETED && task.getStatus() != TaskStatus.CANCELLED)
                .filter(task -> task.getDueDate().isBefore(today))
                .collect(Collectors.toMap(
                        task -> task ,
                        task -> today.toEpochDay() - task.getDueDate().toEpochDay()
                ));
    }
}
