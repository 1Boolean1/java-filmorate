package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
public class FeedRepository extends BaseRepository<Event> {

    public FeedRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public List<Event> getUsersEventFeed(long userId) {
        String sqlQuery = "SELECT * FROM event_feed WHERE user_id = ?;";
        return findMany(sqlQuery, userId);
    }

    public void saveEvent(Event event) {
        String sqlQuery = "INSERT INTO event_feed (timestamp, user_id, event_type, operation, entity_id) " +
                          "VALUES (?, ?, ?, ?, ?);";
        update(sqlQuery, event.getTimestamp(), event.getUserId(), event.getEventType().name(),
                event.getOperation().name(), event.getEntityId());
    }
}
