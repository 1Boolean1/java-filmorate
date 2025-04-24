package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film addFilm(@RequestBody @Valid Film film);

    Film updateFilm(@RequestBody Film film);

    Film getFilmById(long filmId);

    boolean deleteFilm(long filmId);
}
