package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;
    @NotBlank(message = "Invalid email")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "Invalid login")
    private String login;
    private String name;
    @PastOrPresent(message = "Invalid birthday")
    private LocalDate birthday;
    private Set<Integer> friends;

    public User() {
        this.friends = new HashSet<>(); // Инициализация в конструкторе
    }
}
