package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Rating;

@Data
public class NewFilmRequest {
    private String name;
    private String description;
    private String releaseDate;
    private String duration;
    private Rating ratingId;
    private Director directors;
}
