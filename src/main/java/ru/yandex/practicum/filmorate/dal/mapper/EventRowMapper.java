package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = new Event();
        event.setId(resultSet.getLong("event_id"));
        event.setTimestamp(resultSet.getLong("timestamp"));
        event.setUserId(resultSet.getLong("user_id"));
        event.setEventType(EventType.valueOf(resultSet.getString(("event_type"))));
        event.setOperation(Operation.valueOf(resultSet.getString("operation")));
        event.setEntityId(resultSet.getLong("entity_id"));
        event.setEventId(resultSet.getLong("event_id"));
        return event;
    }
}
