package org.example.test.java;

import org.example.model.user.Role;
import org.example.model.user.User;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void testCreateUser_ValidInput_ShouldCreateUser() {
        User createdUser = userService.createUser("Jane Smith", "jane@example.com", Role.MANAGER);
        
        assertNotNull(createdUser);
        assertEquals("Jane Smith", createdUser.getName());
        assertEquals("jane@example.com", createdUser.getEmail());
        assertEquals(Role.MANAGER, createdUser.getRole());
        assertTrue(createdUser.isActive());
        assertNotNull(createdUser.getId());
        assertNotNull(createdUser.getCreatedAt());
    }

    @Test
    void testCreateUser_InvalidEmail_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser("John Doe", "invalid-email", Role.DEVELOPER)
        );
        
        assertEquals("Invalid email address", exception.getMessage());
    }

    @Test
    void testCreateUser_DuplicateEmail_ShouldThrowException() {
        userService.createUser("John Doe", "john@example.com", Role.DEVELOPER);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser("Jane Smith", "john@example.com", Role.MANAGER)
        );
        
        assertEquals("Email address already exists", exception.getMessage());
    }

    @Test
    void testViewUser_ExistingUser_ShouldReturnUser() {
        User createdUser = userService.createUser("John Doe", "john@example.com", Role.DEVELOPER);
        
        User foundUser = userService.viewUser(createdUser.getId());
        
        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals("John Doe", foundUser.getName());
    }

    @Test
    void testViewUser_NonExistingUser_ShouldThrowException() {
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> userService.viewUser("non-existing-id")
        );
        
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testListUsers_SortByName_ShouldReturnSortedList() {
        userService.createUser("Alice", "alice@example.com", Role.DEVELOPER);
        userService.createUser("Bob", "bob@example.com", Role.MANAGER);
        userService.createUser("Charlie", "charlie@example.com", Role.DEVELOPER);
        
        List<User> users = userService.listUsers("name");
        
        assertEquals(3, users.size());
        assertEquals("Alice", users.get(0).getName());
        assertEquals("Bob", users.get(1).getName());
        assertEquals("Charlie", users.get(2).getName());
    }

    @Test
    void testListUsers_SortByEmail_ShouldReturnSortedList() {
        userService.createUser("Alice", "alice@example.com", Role.DEVELOPER);
        userService.createUser("Bob", "bob@example.com", Role.MANAGER);
        userService.createUser("Charlie", "charlie@example.com", Role.DEVELOPER);
        
        List<User> users = userService.listUsers("email");
        
        assertEquals(3, users.size());
        assertEquals("alice@example.com", users.get(0).getEmail());
        assertEquals("bob@example.com", users.get(1).getEmail());
        assertEquals("charlie@example.com", users.get(2).getEmail());
    }

    @Test
    void testListUsers_SortByRole_ShouldReturnSortedList() {
        userService.createUser("Alice", "alice@example.com", Role.DEVELOPER);
        userService.createUser("Bob", "bob@example.com", Role.MANAGER);
        userService.createUser("Charlie", "charlie@example.com", Role.DEVELOPER);
        
        List<User> users = userService.listUsers("role");
        
        assertEquals(3, users.size());
        assertEquals(Role.DEVELOPER, users.get(0).getRole());
        assertEquals(Role.DEVELOPER, users.get(1).getRole());
        assertEquals(Role.MANAGER, users.get(2).getRole());
    }

    @Test
    void testListUsers_InvalidSortField_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.listUsers("invalid")
        );
        
        assertEquals("Invalid sort field", exception.getMessage());
    }

    @Test
    void testUpdateUser_ValidNameAndRole_ShouldUpdateUser() {
        User createdUser = userService.createUser("John Doe", "john@example.com", Role.DEVELOPER);
        
        User updatedUser = userService.updateUser(createdUser.getId(), "Jane Smith", Role.MANAGER);
        
        assertEquals("Jane Smith", updatedUser.getName());
        assertEquals(Role.MANAGER, updatedUser.getRole());
        assertEquals("john@example.com", updatedUser.getEmail());
    }

    @Test
    void testUpdateUser_OnlyName_ShouldUpdateName() {
        User createdUser = userService.createUser("John Doe", "john@example.com", Role.DEVELOPER);
        
        User updatedUser = userService.updateUser(createdUser.getId(), "Jane Smith", null);
        
        assertEquals("Jane Smith", updatedUser.getName());
        assertEquals(Role.DEVELOPER, updatedUser.getRole());
    }

    @Test
    void testUpdateUser_OnlyRole_ShouldUpdateRole() {
        User createdUser = userService.createUser("John Doe", "john@example.com", Role.DEVELOPER);
        
        User updatedUser = userService.updateUser(createdUser.getId(), null, Role.MANAGER);
        
        assertEquals("John Doe", updatedUser.getName());
        assertEquals(Role.MANAGER, updatedUser.getRole());
    }

    @Test
    void testDeleteUser_InactiveUser_ShouldDeactivateUser() {
        User createdUser = userService.createUser("John Doe", "john@example.com", Role.DEVELOPER);
        
        createdUser.deactivate();
        
        userService.deleteUser(createdUser.getId());
        
        assertFalse(createdUser.isActive());
    }
}
