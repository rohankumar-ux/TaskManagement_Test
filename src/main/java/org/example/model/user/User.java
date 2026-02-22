package org.example.model.user;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private final String id;
    private String name;
    private String email;
    private Role role;
    private final LocalDateTime createdAt;
    private boolean active;

    public User(String name , String email , Role role){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getEmail(){
        return email;
    }
    public Role getRole(){
        return role;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public boolean isActive(){
        return active;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setRole(Role role){
        this.role = role;
    }
    public void deactivate(){
        this.active = false;
    }

    @Override
    public String toString(){
        return "User - " +
                "id : " + id + "|" +
                "name : " + name + "|" +
                "email : " + email + "|" +
                "role : " + role + "|" +
                "createdAt : " + createdAt + "|" +
                "active : " + active;

    }
}
