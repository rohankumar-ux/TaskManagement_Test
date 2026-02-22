package org.example.test.java;

import org.example.model.activity.ActivityType;
import org.example.model.task.*;
import org.example.model.user.Role;
import org.example.model.user.User;
import org.example.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.repository.ActivityRepository;
import org.example.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceIntegrationTest {

    private TaskService taskService;
    private TaskRepository taskRepository;
    private ActivityRepository activityRepository;
    private User testUser;
    private User assigneeUser;

    @BeforeEach
    void setUp() {
        taskRepository = new TaskRepository();
        activityRepository = new ActivityRepository();
        taskService = new TaskService(taskRepository, activityRepository);
        
        testUser = new User("John Creator", "john@example.com", Role.MANAGER);
        assigneeUser = new User("Jane Assignee", "jane@example.com", Role.DEVELOPER);
    }

    @Test
    void testCreateTask_ShouldCreateTaskAndLogActivity() {
        Task createdTask = taskService.createTask("Test Task", "Test Description", testUser);
        
        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getTitle());
        assertEquals("Test Description", createdTask.getDescription());
        assertEquals(testUser, createdTask.getCreatedBy());
        assertEquals(TaskStatus.OPEN, createdTask.getStatus());
        assertEquals(Priority.MEDIUM, createdTask.getPriority());
        
        Optional<Task> retrievedTask = taskService.viewTask(createdTask.getId());
        assertTrue(retrievedTask.isPresent());
        assertEquals(createdTask.getId(), retrievedTask.get().getId());
        
        List<ActivityEvent> activities = taskService.getActivityLog(createdTask.getId());
        assertEquals(1, activities.size());
        assertEquals(ActivityType.TASK_CREATED, activities.get(0).getActivityType());
        assertEquals(testUser, activities.get(0).getPerformedBy());
    }

    @Test
    void testUpdateStatus_ValidTransition_ShouldUpdateTaskAndLogActivity() {
        Task task = taskService.createTask("Test Task", "Test Description", testUser);
        
        Task updatedTask = taskService.updateStatus(task.getId(), TaskStatus.IN_PROGRESS, testUser);
        
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
        assertEquals(task.getVersion() + 1, updatedTask.getVersion());
        
        List<ActivityEvent> activities = taskService.getActivityLog(task.getId());
        assertEquals(2, activities.size());
        assertEquals(ActivityType.STATUS_CHANGED, activities.get(1).getActivityType());
        assertTrue(activities.get(1).getDetails().contains("OPEN -> IN_PROGRESS"));
    }

    @Test
    void testUpdateStatus_InvalidTransition_ShouldThrowException() {
        Task task = taskService.createTask("Test Task", "Test Description", testUser);
        
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> taskService.updateStatus(task.getId(), TaskStatus.COMPLETED, testUser)
        );
        
        assertEquals("Invalid Status Transition.", exception.getMessage());
    }

    @Test
    void testAssignTask_ShouldAssignTaskAndLogActivity() {
        Task task = taskService.createTask("Test Task", "Test Description", testUser);
        
        Task assignedTask = taskService.assignTask(task.getId(), assigneeUser, testUser);
        
        assertEquals(assigneeUser, assignedTask.getAssignedTo());
        assertEquals(task.getVersion() + 1, assignedTask.getVersion());
        
        List<ActivityEvent> activities = taskService.getActivityLog(task.getId());
        assertEquals(2, activities.size());
        assertEquals(ActivityType.ASSIGNEE_CHANGED, activities.get(1).getActivityType());
        assertTrue(activities.get(1).getDetails().contains("Unassigned -> jane@example.com"));
    }

    @Test
    void testUnassignTask_ShouldUnassignTaskAndLogActivity() {
        Task task = taskService.createTask("Test Task", "Test Description", testUser);
        taskService.assignTask(task.getId(), assigneeUser, testUser);
        
        Task unassignedTask = taskService.unassignTask(task.getId(), testUser);
        
        assertNull(unassignedTask.getAssignedTo());
        
        List<ActivityEvent> activities = taskService.getActivityLog(task.getId());
        assertEquals(3, activities.size());
        assertEquals(ActivityType.ASSIGNEE_CHANGED, activities.get(2).getActivityType());
        assertTrue(activities.get(2).getDetails().contains("jane@example.com -> Unassigned"));
    }

    @Test
    void testUpdatePriority_ShouldUpdatePriorityAndLogActivity() {
        Task task = taskService.createTask("Test Task", "Test Description", testUser);
        
        Task updatedTask = taskService.updatePriority(task.getId(), Priority.HIGH, testUser);
        
        assertEquals(Priority.HIGH, updatedTask.getPriority());
        assertEquals(task.getVersion() + 1, updatedTask.getVersion());
        
        List<ActivityEvent> activities = taskService.getActivityLog(task.getId());
        assertEquals(2, activities.size());
        assertEquals(ActivityType.PRIORITY_CHANGED, activities.get(1).getActivityType());
        assertTrue(activities.get(1).getDetails().contains("MEDIUM -> HIGH"));
    }

    @Test
    void testUpdateDueDate_ShouldUpdateDueDateAndLogActivity() {
        Task task = taskService.createTask("Test Task", "Test Description", testUser);
        LocalDate dueDate = LocalDate.now().plusWeeks(2);
        
        Task updatedTask = taskService.updateDueDate(task.getId(), dueDate, testUser);
        
        assertEquals(dueDate, updatedTask.getDueDate());
        assertEquals(task.getVersion() + 1, updatedTask.getVersion());
        
        List<ActivityEvent> activities = taskService.getActivityLog(task.getId());
        assertEquals(2, activities.size());
        assertEquals(ActivityType.DUE_DATE_CHANGED, activities.get(1).getActivityType());
        assertTrue(activities.get(1).getDetails().contains("No Deadline -> " + dueDate));
    }

    @Test
    void testAddTag_ShouldAddTagAndLogActivity() {
        Task task = taskService.createTask("Test Task", "Test Description", testUser);
        
        Task updatedTask = taskService.addTag(task.getId(), "urgent", testUser);
        
        assertTrue(updatedTask.getTags().contains("urgent"));
        assertEquals(task.getVersion() + 1, updatedTask.getVersion());
        
        List<ActivityEvent> activities = taskService.getActivityLog(task.getId());
        assertEquals(2, activities.size());
        assertEquals(ActivityType.TAG_ADDED, activities.get(1).getActivityType());
        assertTrue(activities.get(1).getDetails().contains("Added tag : urgent"));
    }

    @Test
    void testAddComment_ShouldAddCommentAndLogActivity() {
        Task task = taskService.createTask("Test Task", "Test Description", testUser);
        
        Task updatedTask = taskService.addComment(task.getId(), "This is a test comment", testUser);
        
        assertEquals(1, updatedTask.getComments().size());
        assertEquals("This is a test comment", updatedTask.getComments().get(0).getText());
        assertEquals(testUser, updatedTask.getComments().get(0).getAuthor());
        assertEquals(task.getVersion() + 1, updatedTask.getVersion());
        
        List<ActivityEvent> activities = taskService.getActivityLog(task.getId());
        assertEquals(2, activities.size());
        assertEquals(ActivityType.COMMENT_ADDED, activities.get(1).getActivityType());
        assertTrue(activities.get(1).getDetails().contains("Comment by : john@example.com"));
    }

    @Test
    void testViewTaskHistory_ShouldReturnAllVersions() {
        Task task = taskService.createTask("Test Task", "Test Description", testUser);
        taskService.updateStatus(task.getId(), TaskStatus.IN_PROGRESS, testUser);
        taskService.assignTask(task.getId(), assigneeUser, testUser);
        
        List<Task> history = taskService.viewTaskHistory(task.getId());
        
        assertEquals(3, history.size());
        assertEquals(3, history.get(0).getVersion());
        assertEquals(2, history.get(1).getVersion());
        assertEquals(1, history.get(2).getVersion());
    }

    @Test
    void testGetActivityLog_ShouldReturnAllActivities() {
        Task task = taskService.createTask("Test Task", "Test Description", testUser);
        taskService.updateStatus(task.getId(), TaskStatus.IN_PROGRESS, testUser);
        taskService.addComment(task.getId(), "Test comment", testUser);
        
        List<ActivityEvent> activities = taskService.getActivityLog(task.getId());
        
        assertEquals(3, activities.size());
        assertEquals(ActivityType.TASK_CREATED, activities.get(0).getActivityType());
        assertEquals(ActivityType.STATUS_CHANGED, activities.get(1).getActivityType());
        assertEquals(ActivityType.COMMENT_ADDED, activities.get(2).getActivityType());
    }

    @Test
    void testGetAllTasks_ShouldReturnAllTasks() {
        Task task1 = taskService.createTask("Task 1", "Description 1", testUser);
        Task task2 = taskService.createTask("Task 2", "Description 2", testUser);
        
        List<Task> allTasks = taskService.getAllTasks();
        
        assertEquals(2, allTasks.size());
        assertTrue(allTasks.stream().anyMatch(t -> t.getId().equals(task1.getId())));
        assertTrue(allTasks.stream().anyMatch(t -> t.getId().equals(task2.getId())));
    }

    @Test
    void testCompleteWorkflow_ShouldHandleFullTaskLifecycle() {
        Task task = taskService.createTask("Complete Feature", "Implement user authentication", testUser);
        
        taskService.updatePriority(task.getId(), Priority.HIGH, testUser);
        taskService.updateDueDate(task.getId(), LocalDate.now().plusDays(7), testUser);
        taskService.assignTask(task.getId(), assigneeUser, testUser);
        taskService.addTag(task.getId(), "backend", testUser);
        taskService.updateStatus(task.getId(), TaskStatus.IN_PROGRESS, testUser);
        taskService.addComment(task.getId(), "Started working on authentication", assigneeUser);
        taskService.updateStatus(task.getId(), TaskStatus.COMPLETED, testUser);
        
        Optional<Task> finalTask = taskService.viewTask(task.getId());
        assertTrue(finalTask.isPresent());
        
        Task taskState = finalTask.get();
        assertEquals("Complete Feature", taskState.getTitle());
        assertEquals(Priority.HIGH, taskState.getPriority());
        assertEquals(assigneeUser, taskState.getAssignedTo());
        assertEquals(TaskStatus.COMPLETED, taskState.getStatus());
        assertTrue(taskState.getTags().contains("backend"));
        assertEquals(1, taskState.getComments().size());
        
        List<ActivityEvent> activities = taskService.getActivityLog(task.getId());
        assertEquals(8, activities.size());
        
        List<Task> history = taskService.viewTaskHistory(task.getId());
        assertEquals(8, history.size());
    }

    @Test
    void testTaskNotFoundOperations_ShouldThrowExceptions() {
        String nonExistentTaskId = "non-existent-id";
        
        assertThrows(IllegalArgumentException.class, 
            () -> taskService.updateStatus(nonExistentTaskId, TaskStatus.IN_PROGRESS, testUser));
        
        assertThrows(IllegalArgumentException.class, 
            () -> taskService.assignTask(nonExistentTaskId, assigneeUser, testUser));
        
        assertThrows(IllegalArgumentException.class, 
            () -> taskService.updatePriority(nonExistentTaskId, Priority.HIGH, testUser));
        
        assertThrows(IllegalArgumentException.class, 
            () -> taskService.updateDueDate(nonExistentTaskId, LocalDate.now(), testUser));
        
        assertThrows(IllegalArgumentException.class, 
            () -> taskService.addTag(nonExistentTaskId, "test", testUser));
        
        assertThrows(IllegalArgumentException.class, 
            () -> taskService.addComment(nonExistentTaskId, "test", testUser));
    }
}
