package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DirectorDto {
    private Long id;
    @NotBlank(message = "Director name is empty")
    private String name;
}
