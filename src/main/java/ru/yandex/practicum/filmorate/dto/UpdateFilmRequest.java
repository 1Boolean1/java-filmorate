package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Rating;

@Data
public class UpdateFilmRequest {
    private String name;
    private String description;
    private String releaseDate;
    private String duration;
    private Rating ratingId;
    private Director director;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return !(releaseDate == null);
    }

    public boolean hasDuration() {
        return !(duration == null);
    }

    public boolean hasRaringId() {
        return !(ratingId == null);
    }
}
