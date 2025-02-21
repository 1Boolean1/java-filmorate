package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.EmptyFieldException;
import ru.yandex.practicum.filmorate.exception.ValidReleaseException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void testAddFilmWithValidData() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm.getId());
        assertEquals("Inception", addedFilm.getName());
        assertEquals("A mind-bending thriller", addedFilm.getDescription());
        assertEquals(LocalDate.of(2010, 7, 16), addedFilm.getReleaseDate());
        assertEquals(148, addedFilm.getDuration());
    }

    @Test
    void testAddFilmWithInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(1894, 12, 28));
        film.setDuration(148);

        Exception exception = assertThrows(ValidReleaseException.class, () -> {
            filmController.addFilm(film);
        });

        assertEquals("Film release date is after 1895", exception.getMessage());
    }

    @Test
    void testUpdateFilmWithValidData() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);
        Film addedFilm = filmController.addFilm(film);

        addedFilm.setDescription("Updated description");
        Film updatedFilm = filmController.updateFilm(addedFilm);

        assertEquals("Updated description", updatedFilm.getDescription());
    }

    @Test
    void testUpdateFilmWithEmptyId() {
        Film film = new Film();
        film.setId(0);
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Exception exception = assertThrows(EmptyFieldException.class, () -> {
            filmController.updateFilm(film);
        });

        assertEquals("ID can't be empty", exception.getMessage());
    }

    @Test
    void testUpdateFilmNotFound() {
        Film film = new Film();
        film.setId(999);
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            filmController.updateFilm(film);
        });

        assertEquals("Film not found", exception.getMessage());
    }
}
