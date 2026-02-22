package org.example.model.task;

public enum TaskStatus {
    OPEN ,
    IN_PROGRESS ,
    COMPLETED ,
    CANCELLED;

    public boolean isValidTransition(TaskStatus newStatus){
        if(this == newStatus){
            return true;
        }

        return switch(this){
            case OPEN -> newStatus == IN_PROGRESS || newStatus == CANCELLED;
            case IN_PROGRESS -> newStatus == CANCELLED || newStatus == COMPLETED;
            case CANCELLED -> newStatus == OPEN;
            case COMPLETED -> false;
        };
    }

    public boolean isTerminal(){
        return this == COMPLETED;
    }
}
