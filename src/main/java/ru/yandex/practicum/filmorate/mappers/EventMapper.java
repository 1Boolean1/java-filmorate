package ru.yandex.practicum.filmorate.mappers;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {
    public static EventDto mapToEventDto(Event event) {
        EventDto eventDto = new EventDto();

        eventDto.setId(eventDto.getId());
        eventDto.setTimestamp(event.getTimestamp());
        eventDto.setUserId(event.getUserId());
        eventDto.setEventType(event.getEventType());
        eventDto.setOperation(event.getOperation());
        eventDto.setEventId(event.getEventId());
        eventDto.setEntityId(event.getEntityId());
        return eventDto;
    }

    public static Event toEventDto(EventDto eventDto) {
        Event event = new Event();

        event.setId(event.getId());
        event.setTimestamp(eventDto.getTimestamp());
        event.setUserId(eventDto.getUserId());
        event.setEventType(eventDto.getEventType());
        event.setOperation(eventDto.getOperation());
        event.setEventId(eventDto.getEventId());
        event.setEntityId(eventDto.getEntityId());
        return event;
    }

    public static List<EventDto> mapToEventDtoList(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }
        return events.stream()
                .map(EventMapper::mapToEventDto)
                .collect(Collectors.toList());
    }

}
