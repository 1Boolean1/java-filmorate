package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRepository.class, UserRowMapper.class})
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;

    @Test
    void testFindUserById_Success() {
        long expectedId;
        String expectedLogin = "testlogin";
        String expectedEmail = "test@user.com";
        LocalDate expectedBirthday = LocalDate.of(1991, 11, 11);

        User user = new User();
        user.setEmail(expectedEmail);
        user.setLogin(expectedLogin);
        user.setBirthday(expectedBirthday);
        user.setName("Test User Name");

        user = userStorage.addUser(user);
        expectedId = user.getId();

        // Act
        User foundUser = assertDoesNotThrow(
                () -> userStorage.getUserById(expectedId),
                "Метод не должен бросать исключение для существующего пользователя"
        );

        assertNotNull(foundUser, "Найденный пользователь не должен быть null");
        assertEquals(expectedId, foundUser.getId(), "ID пользователя должен совпадать");
        assertEquals(expectedLogin, foundUser.getLogin(), "Логин пользователя должен совпадать");
        assertEquals(expectedEmail, foundUser.getEmail(), "Email пользователя должен совпадать");
        assertEquals(expectedBirthday, foundUser.getBirthday(), "Дата рождения должна совпадать");
        assertEquals("Test User Name", foundUser.getName(), "Имя пользователя должно совпадать");
        assertThat(foundUser.getName()).isEqualTo("Test User Name");
    }

    @Test
    void testFindUserById_NotFound() {
        long nonExistentId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userStorage.getUserById(nonExistentId);
        }, "Должно быть выброшено NotFoundException для несуществующего пользователя");

        assertThat(exception.getMessage()).contains(String.valueOf(nonExistentId));
    }

}