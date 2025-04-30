package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
    private Integer useful;
}
