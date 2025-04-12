package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> findUserRequestsPrivate(Long userId) {
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto createUserRequestPrivate(Long userId, Long eventId) {

        log.info("Проверка существования пользователя");
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь " + userId + " не существует"));

        log.info("Проверка существования события");
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие " + eventId + " не существует"));

        log.info("Проверка существования запроса");
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Нельзя добавить повторный запрос");
        }

        log.info("Проверка попытки добавить запрос на участие в собственном событии");
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Нельзя добавить запрос на участие в своем событии");
        }

        log.info("Проверка попытки участия в неопубликованном событии");
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        log.info("Проверка лимита запросов: event.getParticipantLimit() = {}, event.getConfirmedRequests() = {}",
                event.getParticipantLimit(), event.getConfirmedRequests());
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит запросов на участие");
        }

        Request request = new Request();

        if (event.getRequestModeration() && event.getParticipantLimit() > 0) {
            request.setStatus(RequestStatus.PENDING);
            log.info("Установлен статус PENDING");
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
            log.info("Установлен статус CONFIRMED");
        }

        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(requester);

        Request savedRequest = requestRepository.save(request);

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            log.info("Число запросов события до увеличения: {}", event.getConfirmedRequests());
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            log.info("Число запросов события после увеличения: {}", event.getConfirmedRequests());
            eventRepository.save(event);
        }

        return requestMapper.toDto(savedRequest);
    }

    // Отмена своего запроса на участие в событии
    @Override
    public ParticipationRequestDto cancelUserRequestPrivate(Long userId, Long requestId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не существует");
        }

        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос " + requestId + " не существует"));

        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new ValidationException("Пользователь " + userId + " не создавал запрос " + requestId);
        }

        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.toDto(requestRepository.save(request));
    }

}
