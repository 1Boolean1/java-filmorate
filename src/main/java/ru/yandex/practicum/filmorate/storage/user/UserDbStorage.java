package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

@Qualifier("userDbStorage")
@Repository
public class UserDbStorage implements UserStorage {
    private final UserRepository repository;

    public UserDbStorage(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Collection<User> getUsers() {
        return repository.findAll();
    }

    @Override
    public User addUser(User user) {
        return repository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return repository.update(user);
    }

    @Override
    public User getUserById(long userId) {
        return repository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public boolean deleteUser(long userId) {
        return repository.deleteUser(userId);
    }

    public Set<Long> getFriends(long userId) {
        return repository.getFriends(userId);
    }

    public void addFriend(long userId, long friendId) {
        repository.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        repository.deleteFriend(userId, friendId);
    }
}
