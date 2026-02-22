package org.example.repository;

import org.example.model.activity.ActivityType;
import org.example.model.task.ActivityEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ActivityRepository {

    private final Map<String , ActivityEvent> events = new ConcurrentHashMap<>();
    private final Map<String , List<String>> taskEvents = new ConcurrentHashMap<>();

    public void save(ActivityEvent event){
        events.put(event.getEventId(),event);

        taskEvents.computeIfAbsent(event.getTaskId() , k->new ArrayList<>())
                .add(event.getEventId());
    }

    public List<ActivityEvent> findByTaskId(String taskId){
        return taskEvents.getOrDefault(taskId , Collections.emptyList())
                .stream()
                .map(events::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ActivityEvent::getTimestamp))
                .collect(Collectors.toList());
    }

    public List<ActivityEvent> findByActivityType(ActivityType type){
        return events.values().stream()
                .filter(e -> e.getActivityType() == type)
                .sorted(Comparator.comparing(ActivityEvent::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<ActivityEvent> findAll(){
        return events.values().stream()
                .sorted(Comparator.comparing(ActivityEvent::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public Optional<ActivityEvent> findById(String eventId){
        return Optional.ofNullable(events.get(eventId));
    }
}
