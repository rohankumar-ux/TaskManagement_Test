package org.example.service;

import org.example.model.user.User;
import org.example.model.user.Role;
import org.example.repository.UserRepository;
import org.example.util.EmailValidator;

import java.util.Comparator;
import java.util.List;


public class UserService {
    private final UserRepository repository = new UserRepository();

    public User createUser(String name, String email, Role role){
        if (!EmailValidator.isValid(email)) {
            throw new IllegalArgumentException("Invalid email address");
        }
        if(repository.existsByEmail(email)){
            throw new IllegalArgumentException("Email address already exists");
        }
        User user = new User(name,email,role);
        repository.save(user);
        return user;
    }

    public User viewUser(String userId){
        return repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> listUsers(String sortBy){
        List<User> users = repository.findAll();

        Comparator<User> comparator = switch(sortBy.toLowerCase()) {
            case "name" -> Comparator.comparing(User::getName);
            case "email" -> Comparator.comparing(User::getEmail);
            case "role" -> Comparator.comparing(u -> u.getRole().name());
            default -> throw new IllegalArgumentException("Invalid sort field");
        };

        users.sort(comparator);
        System.out.println("Total Users : "+users.size());
        return users;
    }

    public User updateUser(String userId , String name , Role role){
        User user = viewUser(userId);

        if(name != null){
            user.setName(name);
        }
        if(role != null){
            user.setRole(role);
        }
        return user;
    }

    public void deleteUser(String userId){
        User user = viewUser(userId);

        if(user.isActive()){
            throw new RuntimeException("User has active tasks , cannot be deleted");
        }
        user.deactivate();
    }
}
