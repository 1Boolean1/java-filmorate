package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    private long id;
    @NotBlank(message = "Invalid email")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "Invalid login")
    private String login;
    private String name;
    @PastOrPresent(message = "Invalid birthday")
    private LocalDate birthday;
}
