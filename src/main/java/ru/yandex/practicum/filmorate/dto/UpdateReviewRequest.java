package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateReviewRequest {
    @Positive
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    @Positive
    private Long userId;
    @Positive
    private Long filmId;

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }

    public boolean hasRating() {
        return isPositive != null;
    }
}
