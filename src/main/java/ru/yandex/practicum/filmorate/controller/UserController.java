package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EmptyFieldException;
import ru.yandex.practicum.filmorate.exception.InvalidFillingException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("getUsers");
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        if (user.getBirthday() != null) {
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.warn("Invalid birthday");
                throw new InvalidFillingException("Invalid birthday");
            }
        }

        if (user.getName() == null) {
            log.debug("Username is empty, using login as name");
            user.setName(user.getLogin());
        }

        user.setId(setUserId());
        log.debug("User id: {}", user.getId());
        users.put(user.getId(), user);
        log.info("Added user: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        if (user.getId() == 0) {
            log.warn("User id is empty");
            throw new EmptyFieldException("ID can't be empty");
        }

        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            if (user.getName() != null && !user.getName().isBlank()) {
                log.debug("Updating user with name {}", user.getName());
                oldUser.setName(user.getName());
            }

            if (user.getBirthday() != null) {
                log.debug("Updating user with birthday {}", user.getBirthday());
                oldUser.setBirthday(user.getBirthday());
            }

            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                log.debug("Updating user with email {}", user.getEmail());
                oldUser.setEmail(user.getEmail());
            }

            if (user.getLogin() != null && !user.getLogin().isBlank()) {
                if (user.getLogin().contains(" ")) {
                    log.warn("Invalid login");
                    throw new InvalidFillingException("Invalid login");
                }
                log.debug("Updating user with login {}", user.getLogin());
                oldUser.setLogin(user.getLogin());
            }
            log.info("User updated");
            return oldUser;
        }

        log.warn("User not found");
        throw new RuntimeException("User not found");
    }

    private int setUserId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

