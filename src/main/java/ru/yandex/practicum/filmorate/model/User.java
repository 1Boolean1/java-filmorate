package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotBlank(message = "Invalid email")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "Invalid login")
    private String login;
    private String name;
    private LocalDate birthday;
}
