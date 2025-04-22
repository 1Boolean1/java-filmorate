package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository<Director> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM DIRECTORS d";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM DIRECTORS d WHERE d.director_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO DIRECTORS (NAME) VALUES(?)";
    private static final String UPDATE_QUERY = "UPDATE DIRECTORS SET NAME = ? WHERE DIRECTOR_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";

    public DirectorRepository(JdbcTemplate jdbc,
                              RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Director> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Director save(Director director) {
        Long id = (Long) insert(INSERT_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    public Director update(Director director) {
        update(UPDATE_QUERY, director.getName(), director.getId());
        return director;
    }

    public void delete(long id) {
        update(DELETE_QUERY, id);
    }

}
