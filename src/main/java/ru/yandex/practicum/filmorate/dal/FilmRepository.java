package ru.yandex.practicum.filmorate.dal;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private static final String GET_COMMON_FILMS = "SELECT f.*, r.rating_id as mpa_id, r.rating_name as mpa_name " +
            "FROM Films f " +
            "JOIN rating AS r ON f.rating_id = r.rating_id " +
            "JOIN Likes l ON f.id = l.film_id " +
            "WHERE l.film_id IN ( " +
            "    SELECT film_id FROM Likes WHERE user_id = ? " +
            "    INTERSECT " +
            "    SELECT film_id FROM Likes WHERE user_id = ? " +
            ") " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(l.user_id) DESC";

    private static final String FIND_GENRES_FOR_FILMS_QUERY =
            "SELECT fg.film_id, g.id as genre_id, g.name as genre_name " +
                    "FROM genre g " +
                    "JOIN film_genre fg ON g.id = fg.genre_id " +
                    "WHERE fg.film_id IN (:filmIds)";
    private static final String DELETE_FILM_QUERY = "DELETE FROM Films WHERE id = ?";

    private static final String FIND_DIRECTOR_FOR_FILMS_QUERY = """
            SELECT fd.FILM_ID,
              	   d.DIRECTOR_ID,
              	   d.NAME as DIRECTOR_NAME
              FROM FILM_DIRECTORS fd
              JOIN DIRECTORS d ON (fd.DIRECTOR_ID = d.DIRECTOR_ID)
             WHERE fd.film_id in (:filmIds)
            """;

    private static final String FIND_BY_DIRECTOR_SORT_BY_LIKES = """
            SELECT f.*,
                   r.rating_id as mpa_id,
                   r.rating_name as mpa_name
              FROM films AS f
              JOIN rating AS r ON f.rating_id = r.rating_id
              JOIN FILM_DIRECTORS fd ON f.ID = fd.FILM_ID
             WHERE fd.DIRECTOR_ID = ?
             ORDER BY (SELECT COUNT(1) FROM LIKES l WHERE l.FILM_ID = f.ID) DESC
            """;

    private static final String FIND_BY_DIRECTOR_SORT_BY_YEAR = """
                    SELECT f.*,
                           r.rating_id as mpa_id,
                           r.rating_name as mpa_name
                      FROM films AS f
                      JOIN rating AS r ON f.rating_id = r.rating_id
                      JOIN FILM_DIRECTORS fd ON f.ID = fd.FILM_ID
                     WHERE fd.DIRECTOR_ID = ?
                     ORDER BY f.RELEASE_DATE
            """;

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

    private static class FilmDirectorRelation {
        final long filmId;
        final Director director;

        FilmDirectorRelation(long filmId, Director director) {
            this.filmId = filmId;
            this.director = director;
        }
    }

    private final RowMapper<FilmGenreRelation> filmGenreRelationRowMapper = (rs, rowNum) -> {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("genre_name"));
        return new FilmGenreRelation(rs.getLong("film_id"), genre);
    };

    private final RowMapper<FilmDirectorRelation> filmDirectorRelationRowMapper = (rs, rowNum) -> {
        Director director = new Director();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("director_name"));
        return new FilmDirectorRelation(rs.getLong("film_id"), director);
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
            setDirectorForFilm(films);
        }
        return films;
    }

    public List<Film> findCommon(long userId, long filmId) {
        List<Film> films = jdbc.query(GET_COMMON_FILMS, filmWithRatingMapper, userId, filmId);
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
            Film film = films.getFirst();
            setGenresForFilms(List.of(film));
            setDirectorForFilm(List.of(film));
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
        saveDirector(film);
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

        deleteFilms(film.getId());
        saveDirector(film);

        return findById(film.getId()).orElseThrow(() -> new IllegalStateException("Updated film not found, id: " + film.getId()));
    }

    public boolean deleteFilm(long id) {
        return delete(DELETE_FILM_QUERY, id);
    }

    public Map<Long, List<Long>> getAllLikesGroupedByUser() {
        String sql = "SELECT * FROM Likes";

        return jdbc.query(sql, new ResultSetExtractor<Map<Long, List<Long>>>() {
            @Override
            public Map<Long, List<Long>> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<Long, List<Long>> userLikes = new HashMap<>();
                while (rs.next()) {
                    long userId = rs.getLong("user_id");
                    long filmId = rs.getLong("film_id");

                    List<Long> filmIds = userLikes.computeIfAbsent(userId, k -> new ArrayList<>());
                    filmIds.add(filmId);
                }
                return userLikes;
            }
        });
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

    public List<Film> getTopFilms(int count, Integer genreId, Integer year) {

        StringBuilder queryBuilder = new StringBuilder("SELECT f.*, r.rating_id AS mpa_id, r.rating_name AS mpa_name " +
                "FROM films AS f JOIN rating AS r on f.rating_id = r.rating_id " +
                "JOIN film_genre AS fg ON f.id = fg.film_id " +
                "JOIN likes AS l ON f.id = l.film_id WHERE 1=1");

        List<Integer> filterParams = new ArrayList<>();

        if (genreId != -1) {
            queryBuilder.append(" AND fg.genre_id = ?");
            filterParams.add(genreId);
        }

        if (year != -1) {
            queryBuilder.append(" AND EXTRACT(YEAR FROM f.release_date) = ?");
            filterParams.add(year);
        }

        queryBuilder.append(" GROUP BY f.id, r.rating_id, r.rating_name ORDER BY COUNT(l.user_id) DESC LIMIT ?;");
        filterParams.add(count);

        List<Film> films = jdbc.query(queryBuilder.toString(), filmWithRatingMapper, filterParams.toArray());
        setGenresForFilms(films);
        return films;
    }

    public List<Film> findByDirector(long directorId, String sortMode) {
        String sql = "";
        if (sortMode.equals("likes")) {
            sql = FIND_BY_DIRECTOR_SORT_BY_LIKES;
        } else if (sortMode.equals("year")) {
            sql = FIND_BY_DIRECTOR_SORT_BY_YEAR;
        }

        List<Film> films = jdbc.query(sql, filmWithRatingMapper, directorId);

        if (!films.isEmpty()) {
            setGenresForFilms(films);
            setDirectorForFilm(films);
        }
        return films;
    }

    private void saveDirector(Film film) {
        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES(?, ?)";

        List<Object[]> batchArgs = film.getDirectors().stream()
                .filter(Objects::nonNull)
                .filter(director -> director.getId() > 0)
                .distinct()
                .map(director -> new Object[]{film.getId(), director.getId()})
                .collect(Collectors.toList());

        if (!batchArgs.isEmpty()) {
            jdbc.batchUpdate(sql, batchArgs);
        }
    }

    private void setDirectorForFilm(List<Film> films) {
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        if (filmIds.isEmpty()) {
            return;
        }

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filmIds", filmIds);


        List<FilmDirectorRelation> relations = namedJdbcTemplate.query(
                FIND_DIRECTOR_FOR_FILMS_QUERY,
                parameters,
                filmDirectorRelationRowMapper
        );

        Map<Long, List<Director>> directorsByFilmId = relations.stream()
                .collect(groupingBy(
                        relation -> relation.filmId,
                        mapping(relation -> relation.director, toList())
                ));

        films.forEach(film ->
                film.setDirectors(directorsByFilmId.getOrDefault(film.getId(), Collections.emptyList()))
        );
    }

    private void deleteFilms(long filmId) {
        String sql = "DELETE FROM FILM_DIRECTORS WHERE film_id = ?";
        jdbc.update(sql, filmId);
    }

}

