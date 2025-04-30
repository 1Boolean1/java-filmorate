package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewReviewRequest {
    @NotNull
    private String content;
    @NotNull
    private Boolean isPositive;
    /*
    POST тесты в postman требуют возвращать 404 при неверных значения userId и filmId.
    В это время стандартный Spring-обработчик вернет 400 (аннотация @Positive)
    Реализовал обработку вручную в ReviewService с выбрасыванием исключения
     */
    private Long userId;
    private Long filmId;
}
