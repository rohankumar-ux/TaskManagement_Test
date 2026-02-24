package org.example.app;

import org.example.model.task.ActivityEvent;
import org.example.model.task.Priority;
import org.example.model.task.Task;
import org.example.model.task.TaskStatus;
import org.example.model.user.Role;
import org.example.model.user.User;
import org.example.repository.ActivityRepository;
import org.example.repository.TaskRepository;
import org.example.service.TaskSearchService;
import org.example.service.TaskService;
import org.example.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TaskManagementApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final TaskRepository taskRepository = new TaskRepository();
    private static final ActivityRepository activityRepository = new ActivityRepository();
    private static final TaskService taskService = new TaskService(taskRepository, activityRepository);
    private static final TaskSearchService searchService = new TaskSearchService(taskRepository);

    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println(" Task Management System ");

        while (true) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> userManagementMenu();
                case 2 -> taskManagementMenu();
                case 3 -> searchAndFilterMenu();
                case 4 -> {
                    continue;
                }
                case 5 -> {
                    continue;
                }

                case 6 -> {
                    System.out.println("Exiting system.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n MAIN MENU ");
        System.out.println("1. User Management");
        System.out.println("2. Task Management");
        System.out.println("3. Search and Filter Options");
        System.out.println("4. Import and Export");
        System.out.println("5. Reports and Analytics");
        System.out.println("6. Exit");
        System.out.println("================================");
    }

    private static void userManagementMenu() {
        while (true) {
            System.out.println("\n--- User Management ---");
            System.out.println("1. Create User");
            System.out.println("2. View User");
            System.out.println("3. List All Users");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Set Current User");
            System.out.println("7. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> createUser();
                case 2 -> viewUser();
                case 3 -> listUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 6 -> setCurrentUser();
                case 7 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void createUser() {
        System.out.println("\n--- Create New User ---");
        String name = getStringInput("Enter name: ");
        String email = getStringInput("Enter email: ");

        System.out.println("Select role:");
        System.out.println("1. ADMIN");
        System.out.println("2. MANAGER");
        System.out.println("3. DEVELOPER");
        int roleChoice = getIntInput("Enter role choice: ");

        Role role = switch (roleChoice) {
            case 1 -> Role.ADMIN;
            case 2 -> Role.MANAGER;
            case 3 -> Role.DEVELOPER;
            default -> Role.DEVELOPER;
        };

        try {
            User user = userService.createUser(name, email, role);
            System.out.println(" User created successfully: " + user.getName() + " (" + user.getId() + ")");
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void viewUser() {
        String userId = getStringInput("Enter user ID: ");
        try {
            User user = userService.viewUser(userId);
            System.out.println("\n--- User Details ---");
            System.out.println("ID: " + user.getId());
            System.out.println("Name: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Role: " + user.getRole());
            System.out.println("Active: " + user.isActive());
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void listUsers() {
        System.out.println("Sort by: ");
        System.out.println("1. Name");
        System.out.println("2. Email");
        System.out.println("3. Role");
        int sortChoice = getIntInput("Enter choice: ");

        String sortBy = switch (sortChoice) {
            case 1 -> "name";
            case 2 -> "email";
            case 3 -> "role";
            default -> "name";
        };

        try {
            List<User> users = userService.listUsers(sortBy);
            System.out.println("\n--- User List ---");
            for (User user : users) {
                System.out.printf("ID: %s | Name: %s | Email: %s | Role: %s%n",
                        user.getId(), user.getName(), user.getEmail(), user.getRole());
            }
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void updateUser() {
        String userId = getStringInput("Enter user ID: ");
        String name = getStringInput("Enter new name (or press Enter to skip): ");

        System.out.println("Select new role (or 0 to skip):");
        System.out.println("1. ADMIN");
        System.out.println("2. MANAGER");
        System.out.println("3. DEVELOPER");
        int roleChoice = getIntInput("Enter choice: ");

        Role role = null;
        if (roleChoice > 0) {
            role = switch (roleChoice) {
                case 1 -> Role.ADMIN;
                case 2 -> Role.MANAGER;
                case 3 -> Role.DEVELOPER;
                default -> null;
            };
        }

        try {
            User user = userService.updateUser(userId, name.isEmpty() ? null : name, role);
            System.out.println(" User updated successfully: " + user.getName());
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        String userId = getStringInput("Enter user ID to delete: ");
        try {
            userService.deleteUser(userId);
            System.out.println(" User deactivated successfully.");
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void setCurrentUser() {
        String userId = getStringInput("Enter user ID to set as current user: ");
        try {
            currentUser = userService.viewUser(userId);
            System.out.println(" Current user set to: " + currentUser.getName());
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void taskManagementMenu() {
        while (true) {
            System.out.println("\n--- Task Management ---");
            System.out.println("1. Create Task");
            System.out.println("2. View Task");
            System.out.println("3. Update Task Status");
            System.out.println("4. Assign Task");
            System.out.println("5. Unassign Task");
            System.out.println("6. Update Priority");
            System.out.println("7. Update Due Date");
            System.out.println("8. Add Tag");
            System.out.println("9. Add Comment");
            System.out.println("10. View Task History");
            System.out.println("11. View Activity Log");
            System.out.println("12. View All Tasks");
            System.out.println("13. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> createTask();
                case 2 -> viewTask();
                case 3 -> updateTaskStatus();
                case 4 -> assignTask();
                case 5 -> unassignTask();
                case 6 -> updatePriority();
                case 7 -> updateDueDate();
                case 8 -> addTag();
                case 9 -> addComment();
                case 10 -> viewTaskHistory();
                case 11 -> viewActivityLog();
                case 12 -> viewAllTasks();
                case 13 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void createTask() {
        if (!ensureCurrentUser()) return;

        System.out.println("\n--- Create New Task ---");
        String title = getStringInput("Enter task title: ");
        String description = getStringInput("Enter task description: ");

        try {
            Task task = taskService.createTask(title, description, currentUser);
            System.out.println(" Task created successfully: " + task.getTitle() + " (ID: " + task.getId() + ")");
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void viewTask() {
        String taskId = getStringInput("Enter task ID: ");
        try {
            Task task = taskService.viewTask(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
            displayTaskDetails(task);
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void updateTaskStatus() {
        if (!ensureCurrentUser()) return;

        String taskId = getStringInput("Enter task ID: ");
        System.out.println("Select new status:");
        System.out.println("1. OPEN");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. IN_REVIEW");
        System.out.println("4. COMPLETED");
        System.out.println("5. CANCELLED");
        int statusChoice = getIntInput("Enter choice: ");

        TaskStatus status = switch (statusChoice) {
            case 1 -> TaskStatus.OPEN;
            case 2 -> TaskStatus.IN_PROGRESS;
            case 3 -> TaskStatus.COMPLETED;
            case 4 -> TaskStatus.CANCELLED;
            default -> null;
        };

        if (status == null) {
            System.out.println("Invalid status.");
            return;
        }

        try {
            Task task = taskService.updateStatus(taskId, status, currentUser);
            System.out.println(" Task status updated to: " + task.getStatus());
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void assignTask() {
        if (!ensureCurrentUser()) return;

        String assigneeId = getStringInput("Enter assignee user ID: ");

        try {
            User assignee = userService.viewUser(assigneeId);
            System.out.println(" Task assigned to: " + assignee.getName());
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void unassignTask() {
        if (!ensureCurrentUser()) return;

        String taskId = getStringInput("Enter task ID: ");
        try {
            taskService.unassignTask(taskId, currentUser);
            System.out.println(" Task unassigned successfully.");
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void updatePriority() {
        if (!ensureCurrentUser()) return;

        String taskId = getStringInput("Enter task ID: ");
        System.out.println("Select priority:");
        System.out.println("1. LOW");
        System.out.println("2. MEDIUM");
        System.out.println("3. HIGH");
        System.out.println("4. CRITICAL");
        int priorityChoice = getIntInput("Enter choice: ");

        Priority priority = switch (priorityChoice) {
            case 1 -> Priority.LOW;
            case 2 -> Priority.MEDIUM;
            case 3 -> Priority.HIGH;
            case 4 -> Priority.CRITICAL;
            default -> null;
        };

        if (priority == null) {
            System.out.println("Invalid priority.");
            return;
        }

        try {
            Task task = taskService.updatePriority(taskId, priority, currentUser);
            System.out.println(" Task priority updated to: " + task.getPriority());
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void updateDueDate() {
        if (!ensureCurrentUser()) return;

        String taskId = getStringInput("Enter task ID: ");
        String dateStr = getStringInput("Enter due date (YYYY-MM-DD): ");

        try {
            LocalDate dueDate = LocalDate.parse(dateStr);
            Task task = taskService.updateDueDate(taskId, dueDate, currentUser);
            System.out.println(" Task due date updated to: " + task.getDueDate());
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void addTag() {
        if (!ensureCurrentUser()) return;

        String taskId = getStringInput("Enter task ID: ");
        String tag = getStringInput("Enter tag: ");

        try {
            taskService.addTag(taskId, tag, currentUser);
            System.out.println(" Tag added successfully.");
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void addComment() {
        if (!ensureCurrentUser()) return;

        String taskId = getStringInput("Enter task ID: ");
        String comment = getStringInput("Enter comment: ");

        try {
            taskService.addComment(taskId, comment, currentUser);
            System.out.println(" Comment added successfully.");
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void viewTaskHistory() {
        String taskId = getStringInput("Enter task ID: ");
        try {
            List<Task> history = taskService.viewTaskHistory(taskId);
            System.out.println("\n--- Task History (Total versions: " + history.size() + ") ---");
            for (int i = 0; i < history.size(); i++) {
                Task task = history.get(i);
                System.out.printf("Version %d: Status=%s, Updated=%s%n",
                        i + 1, task.getStatus(), task.getUpdatedAt());
            }
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void viewActivityLog() {
        String taskId = getStringInput("Enter task ID: ");
        try {
            List<ActivityEvent> activities = taskService.getActivityLog(taskId);
            System.out.println("\n--- Activity Log (Total: " + activities.size() + ") ---");
            for (ActivityEvent activity : activities) {
                System.out.printf("[%s] %s: %s%n",
                        activity.getTimestamp(),
                        activity.getActivityType(),
                        activity.getDetails());
            }
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void viewAllTasks() {
        try {
            List<Task> tasks = taskService.getAllTasks();
            System.out.println("\n--- All Tasks (Total: " + tasks.size() + ") ---");
            for (Task task : tasks) {
                System.out.printf("ID: %s | Title: %s | Status: %s | Priority: %s%n",
                        task.getId(), task.getTitle(), task.getStatus(), task.getPriority());
            }
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void searchAndFilterMenu() {
        while (true) {
            System.out.println("\n--- Search and Filter Options ---");
            System.out.println("1. Filter by Status");
            System.out.println("2. Filter by Multiple Statuses");
            System.out.println("3. Filter by Priority");
            System.out.println("4. Filter by Multiple Priorities");
            System.out.println("5. Filter by Assignee");
            System.out.println("6. Filter Unassigned Tasks");
            System.out.println("7. Group Tasks by Assignee");
            System.out.println("8. Filter by Creator");
            System.out.println("9. Find Created Between Dates");
            System.out.println("10. Find Completed Between Dates");
            System.out.println("11. Find Modified Between Dates");
            System.out.println("12. Find by Tag");
            System.out.println("13. Find by Any Tag");
            System.out.println("14. Find by All Tags");
            System.out.println("15. Find Overdue Tasks");
            System.out.println("16. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> filterByStatus();
                case 2 -> filterByMultipleStatuses();
                case 3 -> filterByPriority();
                case 4 -> filterByMultiplePriorities();
                case 5 -> filterByAssignee();
                case 6 -> filterUnassigned();
                case 7 -> groupByAssignee();
                case 8 -> filterByCreator();
                case 9 -> findCreatedBetween();
                case 10 -> findCompletedBetween();
                case 11 -> findModifiedBetween();
                case 12 -> findByTag();
                case 13 -> findByAnyTag();
                case 14 -> findByAllTags();
                case 15 -> findOverdueTasks();
                case 16 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void filterByStatus() {
        TaskStatus status = selectStatus();
        if (status == null) return;

        List<Task> tasks = searchService.filterByStatus(status);
        displayTaskList("Tasks with status: " + status, tasks);
    }

    private static void filterByMultipleStatuses() {
        System.out.println("Enter number of statuses to filter: ");
        int count = getIntInput("");
        TaskStatus[] statuses = new TaskStatus[count];

        for (int i = 0; i < count; i++) {
            System.out.println("Select status " + (i + 1) + ":");
            statuses[i] = selectStatus();
        }

        List<Task> tasks = searchService.filterByStatuses(statuses);
        displayTaskList("Tasks with selected statuses", tasks);
    }

    private static void filterByPriority() {
        Priority priority = selectPriority();
        if (priority == null) return;

        List<Task> tasks = searchService.filterByPriority(priority);
        displayTaskList("Tasks with priority: " + priority, tasks);
    }

    private static void filterByMultiplePriorities() {
        System.out.println("Enter number of priorities to filter: ");
        int count = getIntInput("");
        Priority[] priorities = new Priority[count];

        for (int i = 0; i < count; i++) {
            System.out.println("Select priority " + (i + 1) + ":");
            priorities[i] = selectPriority();
        }

        List<Task> tasks = searchService.filterByPriorities(priorities);
        displayTaskList("Tasks with selected priorities", tasks);
    }

    private static void filterByAssignee() {
        String userId = getStringInput("Enter assignee user ID: ");
        try {
            User user = userService.viewUser(userId);
            List<Task> tasks = searchService.filterByAssignee(user);
            displayTaskList("Tasks assigned to: " + user.getName(), tasks);
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void filterUnassigned() {
        List<Task> tasks = searchService.filterUnassignee(null);
        displayTaskList("Unassigned Tasks", tasks);
    }

    private static void groupByAssignee() {
        Map<User, List<Task>> grouped = searchService.groupByAssignee();
        System.out.println("\n--- Tasks Grouped by Assignee ---");
        for (Map.Entry<User, List<Task>> entry : grouped.entrySet()) {
            System.out.println("\nAssignee: " + entry.getKey().getName() + " (" + entry.getValue().size() + " tasks)");
            for (Task task : entry.getValue()) {
                System.out.printf("  - %s [%s]%n", task.getTitle(), task.getStatus());
            }
        }
    }

    private static void filterByCreator() {
        String userId = getStringInput("Enter creator user ID: ");
        try {
            User user = userService.viewUser(userId);
            List<Task> tasks = searchService.filterByCreator(user);
            displayTaskList("Tasks created by: " + user.getName(), tasks);
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static void findCreatedBetween() {
        LocalDateTime start = getDateTime("Enter start date/time (YYYY-MM-DD HH:MM): ");
        LocalDateTime end = getDateTime("Enter end date/time (YYYY-MM-DD HH:MM): ");

        if (start != null && end != null) {
            List<Task> tasks = searchService.findCreatedBetween(start, end);
            displayTaskList("Tasks created between dates", tasks);
        }
    }

    private static void findCompletedBetween() {
        LocalDateTime start = getDateTime("Enter start date/time (YYYY-MM-DD HH:MM): ");
        LocalDateTime end = getDateTime("Enter end date/time (YYYY-MM-DD HH:MM): ");

        if (start != null && end != null) {
            List<Task> tasks = searchService.findCompletedBetween(start, end);
            displayTaskList("Tasks completed between dates", tasks);
        }
    }

    private static void findModifiedBetween() {
        LocalDateTime start = getDateTime("Enter start date/time (YYYY-MM-DD HH:MM): ");
        LocalDateTime end = getDateTime("Enter end date/time (YYYY-MM-DD HH:MM): ");

        if (start != null && end != null) {
            List<Task> tasks = searchService.findModifiedBetween(start, end);
            displayTaskList("Tasks modified between dates", tasks);
        }
    }

    private static void findByTag() {
        String tag = getStringInput("Enter tag: ");
        List<Task> tasks = searchService.findByTag(tag);
        displayTaskList("Tasks with tag: " + tag, tasks);
    }

    private static void findByAnyTag() {
        System.out.println("Enter number of tags: ");
        int count = getIntInput("");
        String[] tags = new String[count];

        for (int i = 0; i < count; i++) {
            tags[i] = getStringInput("Enter tag " + (i + 1) + ": ");
        }

        List<Task> tasks = searchService.findByAnyTag(tags);
        displayTaskList("Tasks with any of the specified tags", tasks);
    }

    private static void findByAllTags() {
        System.out.println("Enter number of tags: ");
        int count = getIntInput("");
        String[] tags = new String[count];

        for (int i = 0; i < count; i++) {
            tags[i] = getStringInput("Enter tag " + (i + 1) + ": ");
        }

        List<Task> tasks = searchService.findByAllTag(tags);
        displayTaskList("Tasks with all specified tags", tasks);
    }

    private static void findOverdueTasks() {
        try {
            Map<Task, Long> overdue = searchService.findOverdueTasks();
            System.out.println("\n--- Overdue Tasks (Total: " + overdue.size() + ") ---");

            overdue.entrySet().stream()
                    .sorted(Map.Entry.<Task, Long>comparingByValue(Comparator.reverseOrder()))
                    .forEach(entry -> {
                        Task task = entry.getKey();
                        Long days = entry.getValue();
                        System.out.printf("ID: %s | %s | Status: %s | Due: %s | Overdue by: %d days%n",
                                task.getId(), task.getTitle(), task.getStatus(), task.getDueDate(), days);
                    });
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    private static boolean ensureCurrentUser() {
        if (currentUser == null) {
            System.out.println(" No current user set. Please set a current user first (User Management > Set Current User).");
            return false;
        }
        return true;
    }

    private static TaskStatus selectStatus() {
        System.out.println("Select status:");
        System.out.println("1. OPEN");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. IN_REVIEW");
        System.out.println("4. COMPLETED");
        System.out.println("5. CANCELLED");
        int choice = getIntInput("Enter choice: ");

        return switch (choice) {
            case 1 -> TaskStatus.OPEN;
            case 2 -> TaskStatus.IN_PROGRESS;
            case 3 -> TaskStatus.COMPLETED;
            case 4 -> TaskStatus.CANCELLED;
            default -> null;
        };
    }

    private static Priority selectPriority() {
        System.out.println("Select priority:");
        System.out.println("1. LOW");
        System.out.println("2. MEDIUM");
        System.out.println("3. HIGH");
        System.out.println("4. CRITICAL");
        int choice = getIntInput("Enter choice: ");

        return switch (choice) {
            case 1 -> Priority.LOW;
            case 2 -> Priority.MEDIUM;
            case 3 -> Priority.HIGH;
            case 4 -> Priority.CRITICAL;
            default -> null;
        };
    }

    private static void displayTaskDetails(Task task) {
        System.out.println("\n--- Task Details ---");
        System.out.println("ID: " + task.getId());
        System.out.println("Title: " + task.getTitle());
        System.out.println("Description: " + task.getDescription());
        System.out.println("Status: " + task.getStatus());
        System.out.println("Priority: " + task.getPriority());
        System.out.println("Assigned To: " + (task.getAssignedTo() != null ? task.getAssignedTo().getName() : "Unassigned"));
        System.out.println("Created By: " + task.getCreatedBy().getName());
        System.out.println("Due Date: " + (task.getDueDate() != null ? task.getDueDate() : "No deadline"));
        System.out.println("Created At: " + task.getCreatedAt());
        System.out.println("Updated At: " + task.getUpdatedAt());
        System.out.println("Tags: " + task.getTags());
    }

    private static void displayTaskList(String title, List<Task> tasks) {
        System.out.println("\n--- " + title + " (Total: " + tasks.size() + ") ---");
        for (Task task : tasks) {
            System.out.printf("ID: %s | %s | Status: %s | Priority: %s | Assigned: %s%n",
                    task.getId(),
                    task.getTitle(),
                    task.getStatus(),
                    task.getPriority(),
                    task.getAssignedTo() != null ? task.getAssignedTo().getName() : "Unassigned");
        }
    }

    private static LocalDateTime getDateTime(String prompt) {
        String input = getStringInput(prompt);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return LocalDateTime.parse(input, formatter);
        } catch (Exception e) {
            System.out.println(" Invalid date format.");
            return null;
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}