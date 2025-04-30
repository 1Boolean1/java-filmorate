package ru.yandex.practicum.filmorate.service.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.FeedRepository;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

@Service
@Slf4j
public class FeedService {

    private final FeedRepository feedRepository;

    @Autowired
    public FeedService(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    @Transactional
    public List<EventDto> getUsersEventFeed(long userId) {
        log.info("Обработка запроса на получение ленты событий пользователя с userId = {}", userId);
        List<Event> events = feedRepository.getUsersEventFeed(userId);
        if (events.isEmpty()) {
            log.error("No data found");
            throw new NotFoundException("No data found");
        }
        return EventMapper.mapToEventDtoList(events);
    }

    @Transactional
    public void logEvent(long userId, EventType eventType, Operation op, long entityId) {
        log.info("Сохранение события = {}, операция = {}, инициатор = {}, субъект = {}", eventType, op, userId, entityId);
        Event event = new Event();
        event.setTimestamp(System.currentTimeMillis());
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperation(op);
        event.setEntityId(entityId);
        feedRepository.saveEvent(event);
    }
}
