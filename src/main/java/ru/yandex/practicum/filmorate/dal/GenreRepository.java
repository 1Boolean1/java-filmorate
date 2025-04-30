package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String GET_GENRES = "SELECT id, name FROM genre ORDER BY id";
    private static final String GET_GENRE_BY_ID = "SELECT id, name FROM genre WHERE id = ?";
    private static final String FIND_GENRES_BY_FILM_ID_QUERY =
            "SELECT g.id, g.name " +
            "FROM genre g JOIN film_genre fg ON g.id = fg.genre_id " +
            "WHERE fg.film_id = ? " +
            "ORDER BY g.id ASC";

    public GenreRepository(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        return findMany(GET_GENRES);
    }

    public Optional<Genre> findById(int id) {
        return findOne(GET_GENRE_BY_ID, id);
    }

    public List<Genre> findGenresByFilmId(long filmId) {
        return findMany(FIND_GENRES_BY_FILM_ID_QUERY, filmId);
    }
}