package org.example.repository;

import org.example.model.task.Task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TaskRepository {

    private final Map<String , List<Task>> taskVersions = new ConcurrentHashMap<>();
    private final Map<String , Task> currTasks = new ConcurrentHashMap<>();

    public void save(Task task){
        String taskId = task.getId();

        taskVersions.computeIfAbsent(taskId , k->new ArrayList<>()).add(task);
        currTasks.put(taskId,task);
    }

    public Optional<Task> findById(String taskId){
        return Optional.ofNullable(currTasks.get(taskId));
    }

    public List<Task> findAll(){
        return new ArrayList<>(currTasks.values());
    }

    public List<Task> findAllVersions(String taskId){
        return taskVersions.getOrDefault(taskId , Collections.emptyList())
                .stream()
                .sorted(Comparator.comparingInt(Task::getVersion).reversed())
                .collect(Collectors.toList());
    }

    public Optional<Task> findVersion(String taskId , int version){
        return taskVersions.getOrDefault(taskId , Collections.emptyList())
                .stream()
                .filter(t -> t.getVersion() == version)
                .findFirst();
    }

    public boolean exists(String taskId){
        return currTasks.containsKey(taskId);
    }
}
