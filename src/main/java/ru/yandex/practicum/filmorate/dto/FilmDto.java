package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class FilmDto {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private RatingDto mpa;
    private List<GenreDto> genres = new ArrayList<>();
    private Set<Long> likes = new HashSet<>();
    private List<DirectorDto> directors = new ArrayList<>();
}