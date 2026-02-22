package org.example.model.task;

import org.example.model.user.User;

import java.time.LocalDateTime;

public class Comment {
    private final User author;
    private final String text;
    private final LocalDateTime timestamp;

    public Comment(User author,String text){
        this.author = author;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    public User getAuthor(){
        return author;
    }

    public String getText(){
        return text;
    }

    public LocalDateTime getTimestamp(){
        return timestamp;
    }
}
