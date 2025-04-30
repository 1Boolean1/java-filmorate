package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.EmptyFieldException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.feed.FeedService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserDbStorage userDbStorage;
    private final FeedService feedService;

    @Autowired
    public UserService(UserDbStorage userDbStorage, FeedService feedService) {
        this.userDbStorage = userDbStorage;
        this.feedService = feedService;
    }

    public void addFriend(long firstUserId, long secondUserId) {
        if (isExistsUser(userDbStorage.getUserById(firstUserId)) || isExistsUser(userDbStorage.getUserById(secondUserId))) {
            log.warn("User not found");
            throw new NotFoundException("User with this id not found");
        }
        if (!userDbStorage.getFriends(firstUserId).contains(secondUserId)) {
            log.debug("add friends");
            userDbStorage.addFriend(firstUserId, secondUserId);
            feedService.logEvent(firstUserId, EventType.FRIEND, Operation.ADD, secondUserId);
        } else if (userDbStorage.getFriends(firstUserId).contains(secondUserId)) {
            log.warn("the friendship was already created");
            throw new RuntimeException("the friendship was already created");
        } else {
            log.warn("friendship can't be created");
            throw new RuntimeException("friendship can't be created");
        }
    }

    public void deleteFriend(long firstUserId, long secondUserId) {
        if (isExistsUser(userDbStorage.getUserById(firstUserId)) || isExistsUser(userDbStorage.getUserById(secondUserId))) {
            log.warn("User not found");
            throw new NotFoundException("User with this id not found");
        }
        if (userDbStorage.getFriends(firstUserId).contains(secondUserId)) {
            log.debug("delete friends");
            userDbStorage.deleteFriend(firstUserId, secondUserId);
            feedService.logEvent(firstUserId, EventType.FRIEND, Operation.REMOVE, secondUserId);
        }
    }

    public List<UserDto> getFriends(long userId) {
        if (isExistsUser(userDbStorage.getUserById(userId))) {
            log.warn("User not found");
            throw new NotFoundException("User with this id not found");
        }
        List<User> friends = new ArrayList<>();
        for (Long friendId : userDbStorage.getFriends(userId)) {
            User friend = userDbStorage.getUserById(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getGeneralFriends(int firstUserId, int secondUserId) {
        if (isExistsUser(userDbStorage.getUserById(firstUserId)) || isExistsUser(userDbStorage.getUserById(secondUserId))) {
            log.warn("User not found");
            throw new NotFoundException("User with this id not found");
        }
        Set<Long> firstUserFriends = userDbStorage.getFriends(firstUserId);

        Set<Long> secondUserFriends = userDbStorage.getFriends(secondUserId);

        firstUserFriends.retainAll(secondUserFriends);

        List<User> commonFriends = new ArrayList<>();
        for (Long friendId : firstUserFriends) {
            User friend = userDbStorage.getUserById(friendId);
            if (friend != null) {
                commonFriends.add(friend);
            }
        }
        return commonFriends.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Collection<UserDto> getUsers() {
        return userDbStorage.getUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Username is empty, using login as name");
            user.setName(user.getLogin());
        }
        return UserMapper.mapToUserDto(userDbStorage.addUser(user));
    }

    public UserDto updateUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        if (user.getId() == 0) {
            log.warn("User id is empty");
            throw new EmptyFieldException("ID can't be empty");
        } else if (isExistsUser(user)) {
            log.warn("User not found");
            throw new NotFoundException("User with this id not found");
        }
        return UserMapper.mapToUserDto(userDbStorage.updateUser(user));
    }

    public UserDto getUserById(@PathVariable long id) {
        log.info("getUserById");
        return UserMapper.mapToUserDto(userDbStorage.getUserById(id));
    }

    @Transactional
    public void deleteUser(long userId) {
        UserDto userDto = getUserById(userId);
        log.info("Deleting user: {}", userDto);

        boolean isDeleted = userDbStorage.deleteUser(userId);

        if (isDeleted) {
            log.info("User deleted successfully");
        } else {
            throw new InternalServerException("User was not deleted due to internal server error");
        }
    }

    private boolean isExistsUser(User user) {
        return userDbStorage.getUsers().stream()
                .noneMatch(userCheck -> userCheck.getId() == user.getId());
    }
}