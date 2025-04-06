package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.EmptyFieldException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    /*private final UserDbStorage userDbStorage = new UserDbStorage(new UserRepository(
            new JdbcTemplate(), new UserRowMapper()));

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userDbStorage);
    }

    @Test
    void testAddUserWithValidData() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User addedUser = userController.addUser(user);

        assertNotNull(addedUser.getId());
        assertEquals("test@example.com", addedUser.getEmail());
        assertEquals("testLogin", addedUser.getLogin());
        assertEquals(LocalDate.of(1990, 1, 1), addedUser.getBirthday());
        assertEquals("testLogin", addedUser.getName());
    }

    @Test
    void testAddUserWithNullName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setName(null);

        User addedUser = userController.addUser(user);

        assertEquals("testLogin", addedUser.getName());
    }

    @Test
    void testUpdateUserWithValidData() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User addedUser = userController.addUser(user);

        addedUser.setName("Updated Name");
        addedUser.setEmail("updated@example.com");
        User updatedUser = userController.updateUser(addedUser);

        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void testUpdateUserWithEmptyId() {
        User user = new User();
        user.setId(0);
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Exception exception = assertThrows(EmptyFieldException.class, () -> {
            userController.updateUser(user);
        });

        assertEquals("ID can't be empty", exception.getMessage());
    }

    @Test
    void testUpdateUserNotFound() {
        User user = new User();
        user.setId(999);
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userController.updateUser(user);
        });

        assertEquals("User not found", exception.getMessage());
    }*/
}
