package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_LOGIN_QUERY = "SELECT * FROM users WHERE login = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";

    public UserRepository(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public Optional<User> findByLogin(String login) {
        return findOne(FIND_BY_LOGIN_QUERY, login);
    }

    public User save(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    public boolean deleteUser(long id) {
        return delete(DELETE_USER_QUERY, id);
    }

    public Set<Long> getFriends(long userId) {
        String sql = "SELECT friend_id FROM friendship WHERE user_id = ?";

        List<Map<String, Object>> rows = jdbc.queryForList(sql, userId);
        Set<Long> friends = new HashSet<>();

        for (Map<String, Object> row : rows) {
            long friendId = (Long) row.get("friend_id");
            friends.add(friendId);
        }

        return friends;
    }

    public void addFriend(long userId, long friendId) {
        String updateSql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        jdbc.update(updateSql, userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        String deleteSql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        update(deleteSql, userId, friendId);
    }
}
