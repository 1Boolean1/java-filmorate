package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_QUERY = "SELECT f.*, r.rating_id as mpa_id, r.rating_name as mpa_name FROM films AS f JOIN rating AS r ON f.rating_id = r.rating_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, r.rating_id as mpa_id, r.rating_name as mpa_name FROM films AS f JOIN rating AS r ON f.rating_id = r.rating_id WHERE f.id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String GET_LIKES_QUERY = "SELECT user_id FROM Likes WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO Likes (user_id, film_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM Likes WHERE film_id = ? AND user_id = ?";

    private static final String FIND_GENRES_FOR_FILMS_QUERY =
            "SELECT fg.film_id, g.id as genre_id, g.name as genre_name " +
                    "FROM genre g " +
                    "JOIN film_genre fg ON g.id = fg.genre_id " +
                    "WHERE fg.film_id IN (:filmIds)";

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private FilmRowMapper filmMapper;

    private static class FilmGenreRelation {
        final long filmId;
        final Genre genre;

        FilmGenreRelation(long filmId, Genre genre) {
            this.filmId = filmId;
            this.genre = genre;
        }
    }

    private final RowMapper<FilmGenreRelation> filmGenreRelationRowMapper = (rs, rowNum) -> {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("genre_name"));
        return new FilmGenreRelation(rs.getLong("film_id"), genre);
    };

    public FilmRepository(JdbcTemplate jdbc,
                          NamedParameterJdbcTemplate namedJdbcTemplate,
                          FilmRowMapper filmMapper) {
        super(jdbc, filmMapper);
        this.jdbc = jdbc;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.filmMapper = filmMapper;
    }

    private final RowMapper<Film> filmWithRatingMapper = (rs, rowNum) -> {
        Film film = filmMapper.mapRow(rs, rowNum);
        Rating mpa = new Rating();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);
        film.setGenres(new ArrayList<>());
        return film;
    };


    public List<Film> findAll() {
        List<Film> films = jdbc.query(FIND_ALL_QUERY, filmWithRatingMapper);

        if (!films.isEmpty()) {
            setGenresForFilms(films);
        }
        return films;
    }

    public Optional<Film> findById(long id) {
        List<Film> films = jdbc.query(FIND_BY_ID_QUERY, filmWithRatingMapper, id);

        if (films.isEmpty()) {
            return Optional.empty();
        } else {
            Film film = films.get(0);
            setGenresForFilms(List.of(film));
            return Optional.of(film);
        }
    }

    private void setGenresForFilms(List<Film> films) {
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        if (filmIds.isEmpty()) {
            return;
        }

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filmIds", filmIds);


        List<FilmGenreRelation> relations = namedJdbcTemplate.query(
                FIND_GENRES_FOR_FILMS_QUERY,
                parameters,
                filmGenreRelationRowMapper
        );

        Map<Long, List<Genre>> genresByFilmId = relations.stream()
                .collect(groupingBy(
                        relation -> relation.filmId,
                        mapping(relation -> relation.genre, toList())
                ));

        films.forEach(film ->
                film.setGenres(genresByFilmId.getOrDefault(film.getId(), Collections.emptyList()))
        );
    }

    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null);
            ps.setInt(4, film.getDuration());
            if (film.getMpa() == null || film.getMpa().getId() == 0) {
                throw new IllegalArgumentException("Film Rating (MPA) ID cannot be null or zero");
            }
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(id);

        saveGenres(film);

        return findById(id).orElseThrow(() -> new IllegalStateException("Saved film not found, id: " + id));
    }

    public Film update(Film film) {
        int updatedRows = jdbc.update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        if (updatedRows == 0) {
            throw new NoSuchElementException("Film with id " + film.getId() + " not found for update.");
        }

        deleteGenres(film.getId());
        saveGenres(film);

        return findById(film.getId()).orElseThrow(() -> new IllegalStateException("Updated film not found, id: " + film.getId()));
    }

    private void deleteGenres(long filmId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbc.update(sql, filmId);
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";

        List<Object[]> batchArgs = film.getGenres().stream()
                .filter(Objects::nonNull)
                .filter(genre -> genre.getId() > 0)
                .distinct()
                .map(genre -> new Object[]{film.getId(), genre.getId()})
                .collect(Collectors.toList());
        if (!batchArgs.isEmpty()) {
            jdbc.batchUpdate(sql, batchArgs);
        }
    }

    public Set<Long> getLikes(long filmId) {
        List<Long> likesList = jdbc.queryForList(GET_LIKES_QUERY, Long.class, filmId);
        return new HashSet<>(likesList);
    }

    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, userId, filmId);
    }

    public void removeLike(long filmId, long userId) {
        jdbc.update(REMOVE_LIKE_QUERY, filmId, userId);
    }
}