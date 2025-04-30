package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Review {
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    @NotNull
    private Long userId;
    private Long filmId;
    private Integer useful;
}
