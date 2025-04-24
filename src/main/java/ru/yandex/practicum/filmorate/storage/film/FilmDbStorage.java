package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.RatingRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository filmRepository;
    private final RatingRepository ratingRepository;
    private final GenreRepository genreRepository;

    public FilmDbStorage(FilmRepository filmRepository,
                         RatingRepository ratingRepository,
                         GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.ratingRepository = ratingRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public Collection<Film> getFilms() {
        return filmRepository.findAll();
    }

    @Override
    public Film addFilm(Film film) {
        validateRatingExists(film.getMpa());
        validateGenresExist(film.getGenres());

        return filmRepository.save(film);
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());

        validateRatingExists(film.getMpa());
        validateGenresExist(film.getGenres());

        Film updatedFilm = filmRepository.update(film);
        if (updatedFilm == null) {
            throw new NotFoundException("Film with id " + film.getId() + " could not be updated (possibly deleted concurrently).");
        }
        return updatedFilm;
    }

    @Override
    public Film getFilmById(long filmId) {
        return filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id " + filmId + " not found"));
    }

    @Override
    public boolean deleteFilm(long filmId) {
        return filmRepository.deleteFilm(filmId);
    }

    public Set<Long> getLikes(long filmId) {
        getFilmById(filmId);
        return filmRepository.getLikes(filmId);
    }

    public void addLike(long filmId, long userId) {
        filmRepository.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        filmRepository.removeLike(filmId, userId);
    }

    public Collection<Genre> getGenres() {
        return genreRepository.findAll();
    }

    public Genre getGenreById(int id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Genre with id " + id + " not found"));
    }

    public Collection<Rating> getRatings() {
        return ratingRepository.findAll();
    }

    public Rating getRatingById(int id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rating with id " + id + " not found"));
    }

    public List<Film> getTopFilms(int count, int genreId, int year) {
        return filmRepository.getTopFilms(count, genreId, year);
    }

    private void validateRatingExists(Rating mpa) {
        if (mpa == null || mpa.getId() == 0) {
            throw new IllegalArgumentException("Film Rating (MPA) is required.");
        }
        getRatingById(mpa.getId());
    }

    private void validateGenresExist(List<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                if (genre == null || genre.getId() == 0) {
                    throw new IllegalArgumentException("Genre ID is required within the genres set.");
                }
                getGenreById(genre.getId());
            }
        }
    }

    public Collection<Film> getRecommendations(long targetUserId) {
        Optional<Long> mostSimilarUserOpt = findUserWithMostCommonLikes(targetUserId);

        if (mostSimilarUserOpt.isEmpty()) {
            return Collections.emptyList();
        }
        long mostSimilarUserId = mostSimilarUserOpt.get();

        Map<Long, List<Long>> allUserLikes = filmRepository.getAllLikesGroupedByUser();

        Set<Long> filmsLikedByTarget = new HashSet<>(allUserLikes.getOrDefault(targetUserId, Collections.emptyList()));

        List<Long> filmsLikedBySimilar = allUserLikes.getOrDefault(mostSimilarUserId, Collections.emptyList());

        List<Long> recommendedFilmIds = filmsLikedBySimilar.stream()
                .filter(filmId -> !filmsLikedByTarget.contains(filmId))
                .collect(Collectors.toList());

        if (recommendedFilmIds.isEmpty()) {
            return Collections.emptyList();
        }

        return recommendedFilmIds.stream()
                .map(filmId -> {
                    try {
                        return getFilmById(filmId);
                    } catch (NotFoundException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<Long> findUserWithMostCommonLikes(long id) {
        Map<Long, Integer> crossesFilms = findCrosses(id);

        if (crossesFilms == null || crossesFilms.isEmpty()) {
            return Optional.empty();
        }

        Optional<Map.Entry<Long, Integer>> maxEntry = crossesFilms.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        return maxEntry.map(Map.Entry::getKey);
    }


    private Map<Long, Integer> findCrosses(long targetUserId) {
        Map<Long, List<Long>> allUserLikes = filmRepository.getAllLikesGroupedByUser();

        List<Long> filmsLikedByTargetUser = allUserLikes.get(targetUserId);

        if (filmsLikedByTargetUser == null || filmsLikedByTargetUser.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<Long> targetLikedSet = new HashSet<>(filmsLikedByTargetUser);

        Map<Long, Integer> commonLikesCountMap = new HashMap<>();

        for (Map.Entry<Long, List<Long>> entry : allUserLikes.entrySet()) {
            long otherUserId = entry.getKey();
            List<Long> filmsLikedByOtherUser = entry.getValue();

            if (otherUserId == targetUserId) {
                continue;
            }

            if (filmsLikedByOtherUser == null || filmsLikedByOtherUser.isEmpty()) {
                continue;
            }

            int commonCount = 0;
            for (Long filmLikedByOther : filmsLikedByOtherUser) {
                if (targetLikedSet.contains(filmLikedByOther)) {
                    commonCount++;
                }
            }

            if (commonCount > 0) {
                commonLikesCountMap.put(otherUserId, commonCount);
            }
        }
        return commonLikesCountMap;
    }
}