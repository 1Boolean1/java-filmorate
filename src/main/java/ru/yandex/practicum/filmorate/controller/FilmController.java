package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Validated
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> getFilms() {
        log.info("Received GET /films request");
        return filmService.getFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto addFilm(@Valid @RequestBody Film film) {
        log.info("Received POST /films request with body: {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody Film film) {
        log.info("Received PUT /films request with body: {}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilmById(@PathVariable long filmId) {
        log.info("Received GET /films/{} request", filmId);
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLikeToFilm(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Received PUT /films/{}/like/{} request", filmId, userId);
        filmService.addLikeToFilm(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLikeToFilm(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Received DELETE /films/{}/like/{} request", filmId, userId);
        filmService.deleteLikeToFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getTopFilms(@RequestParam(defaultValue = "10") @Positive(message = "Count must be positive") int count) {
        log.info("Received GET /films/popular?count={} request", count);
        return filmService.getTopFilms(count);
    }

    //TODO добавить проверку на корректность параметра sortMode
    @GetMapping("/director/{directorId}")
    public List<FilmDto> findByDirector(@PathVariable long directorId,
                                        @RequestParam(name = "sortBy") String sortMode) {
        log.info("Received GET /films/director/{}?sortBy={} request", directorId, sortMode);
        return filmService.findByDirector(directorId, sortMode);
    }

}