package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @Autowired
    public UserController(UserDbStorage userDbStorage, FilmService filmService) {
        userService = new UserService(userDbStorage);
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        log.info("getUsers");
        return userService.getUsers();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto addUser(@RequestBody @Valid User user) {
        log.info("addUser");
        return userService.addUser(user);
    }

    @PutMapping
    public UserDto updateUser(@RequestBody @Valid User user) {
        log.info("updateUser");
        System.out.println(user);
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        log.info("getUserById");
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("addFriend");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("deleteFriend");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getGeneralFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("getGeneralFriends");
        return userService.getGeneralFriends(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable int id) {
        log.info("getFriends");
        return userService.getFriends(id);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int userId) {
        log.info("Recieved DELETE /users/{} request", userId);
        userService.deleteUser(userId);
    }
  
    @GetMapping("/{id}/recommendations")
    public Collection<FilmDto> getRecommendations(@PathVariable long id) {
        log.info("get recommendations");
        return filmService.getRecommendations(id);
    }
}

