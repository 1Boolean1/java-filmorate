package ru.yandex.practicum.filmorate.service.film;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exception.ConstraintViolationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.mappers.RatingMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    public Collection<FilmDto> getFilms() {
        log.info("Getting all films");
        Collection<Film> films = filmStorage.getFilms();
        return films.stream()
                .map(film -> FilmMapper.mapToFilmDto(film, filmStorage.getLikes(film.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public FilmDto addFilm(Film film) {
        log.info("Adding new film: {}", film);
        validateFilm(film);
        Film addedFilm = filmStorage.addFilm(film);
        log.info("Film added with id: {}", addedFilm.getId());
        return FilmMapper.mapToFilmDto(addedFilm, Set.of());
    }

    @Transactional
    public FilmDto updateFilm(Film film) {
        log.info("Updating film with id: {}", film.getId());
        if (film.getId() <= 0) {
            throw new NotFoundException("Film ID must be positive for update.");
        }
        validateFilm(film);
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Film updated: {}", updatedFilm);
        return FilmMapper.mapToFilmDto(updatedFilm, filmStorage.getLikes(updatedFilm.getId()));
    }

    public FilmDto getFilmById(long filmId) {
        log.info("Getting film by id: {}", filmId);
        Film film = filmStorage.getFilmById(filmId);
        return FilmMapper.mapToFilmDto(film, filmStorage.getLikes(filmId));
    }

    @Transactional
    public void addLikeToFilm(long filmId, long userId) {
        log.info("Adding like to film {} from user {}", filmId, userId);
        filmExists(filmId);
        userExists(userId);
        filmStorage.addLike(filmId, userId);
        log.info("Like added successfully");
    }

    @Transactional
    public void deleteLikeToFilm(long filmId, long userId) {
        log.info("Deleting like from film {} by user {}", filmId, userId);
        filmExists(filmId);
        userExists(userId);
        filmStorage.removeLike(filmId, userId);
        log.info("Like removed successfully");
    }

    public List<FilmDto> getTopFilms(int count) {
        log.info("Getting top {} popular films", count);
        if (count <= 0) {
            throw new ValidationException("Count parameter must be positive.");
        }
        Collection<Film> allFilms = filmStorage.getFilms();

        return allFilms.stream()
                .map(film -> {
                    Set<Long> likes = filmStorage.getLikes(film.getId());
                    return Map.entry(film, likes.size());
                })
                .sorted(Map.Entry.<Film, Integer>comparingByValue().reversed())
                .limit(count)
                .map(Map.Entry::getKey)
                .map(film -> FilmMapper.mapToFilmDto(film, filmStorage.getLikes(film.getId())))
                .collect(Collectors.toList());
    }

    public Collection<GenreDto> getGenres() {
        log.info("Getting all genres");
        return filmStorage.getGenres().stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto getGenreById(int id) {
        log.info("Getting genre by id: {}", id);
        Genre genre = filmStorage.getGenreById(id);
        return GenreMapper.mapToGenreDto(genre);
    }

    public Collection<RatingDto> getRatings() {
        log.info("Getting all MPA ratings");
        return filmStorage.getRatings().stream()
                .map(RatingMapper::mapToRatingDto)
                .collect(Collectors.toList());
    }

    public RatingDto getRatingById(int id) {
        log.info("Getting MPA rating by id: {}", id);
        Rating rating = filmStorage.getRatingById(id);
        return RatingMapper.mapToRatingDto(rating);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Validation failed");
            throw new ConstraintViolationException("Film release date cannot be earlier than " + MIN_RELEASE_DATE);
        }
    }

    private void userExists(long userId) {
        userStorage.getUserById(userId);
    }

    private void filmExists(long filmId) {
        filmStorage.getFilmById(filmId);
    }

    public List<FilmDto> findByDirector(long directorId, String sortMode) {
        log.info("Getting films by director with id: {} and sort mode {}", directorId, sortMode);
        Collection<Film> films = filmStorage.findByDirector(directorId, sortMode);
        return films.stream()
                .map(film -> FilmMapper.mapToFilmDto(film, filmStorage.getLikes(film.getId())))
                .collect(Collectors.toList());
    }

}