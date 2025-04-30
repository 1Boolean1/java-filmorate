package ru.yandex.practicum.filmorate.mappers;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class FilmMapper {
    public static Film mapToFilm(Film requestFilm) {
        Film film = new Film();
        film.setId(requestFilm.getId());
        film.setName(requestFilm.getName());
        film.setDescription(requestFilm.getDescription());
        film.setReleaseDate(requestFilm.getReleaseDate());
        film.setDuration(requestFilm.getDuration());

        if (requestFilm.getMpa() != null && requestFilm.getMpa().getId() != 0) {
            film.setMpa(requestFilm.getMpa());
        } else {
            film.setMpa(null);
        }

        if (requestFilm.getGenres() != null) {
            List<Genre> genresWithIdOnly = requestFilm.getGenres().stream()
                    .filter(g -> g != null && g.getId() != 0)
                    .collect(Collectors.toList());
            film.setGenres(genresWithIdOnly);
        } else {
            film.setGenres(new ArrayList<>());
        }

        if (requestFilm.getDirectors() != null) {
            List<Director> directorsWithIdOnly = requestFilm.getDirectors()
                    .stream()
                    .filter(d -> d != null && d.getId() != 0)
                    .collect(Collectors.toList());
            film.setDirectors(directorsWithIdOnly);
        } else {
            film.setDirectors(new ArrayList<>());
        }

        return film;
    }

    public static FilmDto mapToFilmDto(Film film, Set<Long> likes) {
        if (film == null) return null;
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpa(RatingMapper.mapToRatingDto(film.getMpa()));
        dto.setGenres(GenreMapper.mapToGenreDtoList(film.getGenres()));
        dto.setLikes(Optional.ofNullable(likes).orElse(Collections.emptySet()));
        dto.setDirectors(DirectorMapper.mapToDirectorDtoList(film.getDirectors()));
        return dto;
    }


    public static Film mapToFilmDto(FilmDto filmDto) {
        if (filmDto == null) return null;
        Film film = new Film();
        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());
        film.setMpa(RatingMapper.mapToRating(filmDto.getMpa()));
        film.setGenres(GenreMapper.mapToGenreList(filmDto.getGenres()));
        //film.setLikes(Optional.ofNullable(likes).orElse(Collections.emptySet()));
        film.setDirectors(DirectorMapper.mapToDirectorList(filmDto.getDirectors()));
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        return mapToFilmDto(film, Collections.emptySet());
    }
}