package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EmptyFieldException;
import ru.yandex.practicum.filmorate.exception.ValidReleaseException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("getFilms");
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("addFilm");

        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.warn("Film release date is after 1895");
                throw new ValidReleaseException("Film release date is after 1895");
            }
        }

        film.setId(setFilmId());
        log.debug("Film id set to {}", film.getId());
        films.put(film.getId(), film);
        log.info("Film added");
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getId() == 0) {
            log.warn("Film id is empty");
            throw new EmptyFieldException("ID can't be empty");
        }
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            if (film.getName() != null && !film.getName().isBlank()) {
                log.debug("Updating film with name {}", film.getName());
                oldFilm.setName(film.getName());
            }
            if (film.getDescription() != null && !film.getDescription().isBlank()) {
                log.debug("Updating film with description {}", film.getDescription());
                oldFilm.setDescription(film.getDescription());
            }
            if (film.getReleaseDate() != null) {
                log.debug("Updating film with releaseDate {}", film.getReleaseDate());
                oldFilm.setReleaseDate(film.getReleaseDate());
            }
            if (film.getDuration() != 0) {
                log.debug("Updating film with duration {}", film.getDuration());
                oldFilm.setDuration(film.getDuration());
            }
            log.info("Film updated");
            return oldFilm;
        }
        log.warn("Film not found");
        throw new RuntimeException("Film not found");
    }

    private int setFilmId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}