package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getUsers();

    User addUser(@RequestBody @Valid User user);

    User updateUser(@RequestBody User user);

    User getUserById(long userId);

    boolean deleteUser(long userId);
}
