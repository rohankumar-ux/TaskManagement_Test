package org.example.repository;

import org.example.model.user.User;

import java.util.*;

public class UserRepository {
    private final Map<String , User> users = new HashMap<>();

    public void save(User user){
        users.put(user.getId(), user);
    }

    public Optional<User> findById(String id){
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll(){
        return new ArrayList<>(users.values());
    }

    public boolean existsByEmail(String email){
        return users.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }
}
