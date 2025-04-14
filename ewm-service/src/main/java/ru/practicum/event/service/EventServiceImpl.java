package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.mapper.UpdateEventMapper;
import ru.practicum.event.model.*;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.event.service.specification.DbSpecifications;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.EndpointHitDtoRequest;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatDtoResponse;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {

    final RequestMapper requestMapper;
    final RequestRepository requestRepository;
    final EventRepository eventRepository;
    final CategoryRepository categoryRepository;
    final UserRepository userRepository;
    final LocationRepository locationRepository;
    final EventMapper eventMapper;
    final LocationMapper locationMapper;
    final StatsClient statsClient;

    @Override
    public List<EventShortDto> findEventsPrivate(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events;

        events = eventRepository.findAllByInitiatorId(userId, pageable).getContent();

        return events.stream()
                .map(eventMapper::toShortDto)
                .toList();
    }

    @Override
    public List<EventShortDto> findEventsPublic(String text, List<Integer> categories, Boolean paid,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                Boolean onlyAvailable, String sort, Integer from, Integer size,
                                                HttpServletRequest httpServletRequest) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Время начала позже времени окончания");
        }

        Specification<Event> spec = DbSpecifications.getSpecificationPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);

        EventSort eventSort = sort != null ? EventSort.valueOf(sort.toUpperCase()) : null;
        Sort sorting = Sort.unsorted();
        if (eventSort != null) {
            if (eventSort == EventSort.EVENT_DATE) {
                sorting = Sort.by(Sort.Direction.DESC, "eventDate");
            } else if (eventSort == EventSort.VIEWS) {
                sorting = Sort.by(Sort.Direction.DESC, "views");
            }
        }

        hit(httpServletRequest);

        Pageable pageable = PageRequest.of(from / size, size, sorting);
        List<Event> events = eventRepository.findAll(spec, pageable).getContent();

        return eventMapper.toShortDto(events);

    }

    @Override
    public List<EventFullDto> findAdminEvents(List<Integer> users, List<State> states, List<Integer> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Integer from, Integer size) {

        Specification<Event> spec = DbSpecifications.getSpecificationAdmin(users, states, categories, rangeStart, rangeEnd);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAll(spec, pageable).getContent();
        log.info("Длина списка events = {}", events.size());

        return events.stream()
                .map(eventMapper::toFullDto)
                .toList();
    }

    @Override
    public EventFullDto findEventByIdPublic(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("findEventByIdPublic: Событие " + eventId + " не найдено"));

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("findEventByIdPublic: Событие " + eventId + " не опубликовано");
        }

        hit(httpServletRequest);

        List<ViewStatDtoResponse> stats = statsClient.findStats(event.getPublishedOn(),
                LocalDateTime.now(), List.of("/events/" + eventId), true);
        log.info("Метод findEventByIdPublic, длина списка stats: {}", stats.size());
        Long views = stats.isEmpty() ? 0L : stats.getFirst().getHits();
        event.setViews(views);

        log.info("Метод findEventByIdPublic, количество сохраняемых просмотров: {}", views);

        return eventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto createEventPrivate(Long userId, NewEventDto dto) {
        log.info("Создание события");
        if (LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .isBefore(LocalDateTime.now())) {
            throw new ValidationException("Указана неверная дата события");
        }

        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория " + dto.getCategory() + " не найдена"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден"));

        Location location = locationRepository.save(locationMapper.toEntity(dto.getLocation()));

        Event entity = eventMapper.toEntity(dto, category, user);
        entity.setCreatedOn(LocalDateTime.now());
        entity.setConfirmedRequests(0L);
        entity.setLocation(location);
        entity.setState(State.PENDING);

        Event savedEvent = eventRepository.save(entity);
        log.info("Создано событие {}", savedEvent.getId());

        return eventMapper.toFullDto(savedEvent);
    }

    @Override
    public EventFullDto findEventByUserPrivate(Long userId, Long eventId) {
        Optional<Event> event = eventRepository.findByInitiatorIdAndId(userId, eventId);
        if (event.isEmpty()) {
            throw new NotFoundException("Событие " + eventId + " не найдено");
        }
        return eventMapper.toFullDto(event.get());
    }

    @Override
    public EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest dto) {
        log.info("Private: Обновление события {}", eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id = " + eventId + " не найдено."));

        checkEventUpdatePrivate(event, userId, eventId);

        event = UpdateEventMapper.updateEventPrivate(event, dto, categoryRepository,
                locationRepository, locationMapper);

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
                case REJECT_EVENT -> event.setState(State.REJECT);
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
                case PUBLISH_EVENT -> event.setState(State.PUBLISHED);
            }
        }

        return eventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> findEventRequestsPrivate(Long userId, Long eventId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не существует");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id = " + eventId + " не найдено."));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException("Пользователь " + userId + " не является создателем события " + eventId);
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream()
                .map(requestMapper::toDto)
                .toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestPrivate(Long userId, Long eventId,
                                                                    EventRequestStatusUpdateRequest dto) {

        log.info("Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя");

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id = " + eventId + " не найдено."));

        checkUpdateEventRequestPrivate(event, userId, eventId, dto);

        List<Request> requests = requestRepository.findAllById(dto.getRequestIds());
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        requests.forEach(request -> {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Статус заявки не в состоянии ожидания");
            }

            if (event.getConfirmedRequests() < event.getParticipantLimit() && dto.getStatus() == RequestStatus.CONFIRMED) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(request);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
        });

        eventRepository.save(event);
        requestRepository.saveAll(requests);

        List<ParticipationRequestDto> confirmedDtoList = confirmedRequests.stream()
                .map(requestMapper::toDto)
                .toList();

        List<ParticipationRequestDto> rejectedDtoList = rejectedRequests.stream()
                .map(requestMapper::toDto)
                .toList();

        log.info("confirmedDtoList {}", confirmedDtoList.size());
        log.info("rejectedDtoList {}", rejectedDtoList.size());

        return new EventRequestStatusUpdateResult(confirmedDtoList, rejectedDtoList);
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest dto) {
        log.info("Admin: Обновление события");
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id = " + eventId + " не найдено."));

        event = UpdateEventMapper.updateEventAdmin(event, dto, categoryRepository, locationRepository, locationMapper);

        return eventMapper.toFullDto(eventRepository.save(event));
    }

    private void hit(HttpServletRequest httpServletRequest) {
        EndpointHitDtoRequest hitRequest = new EndpointHitDtoRequest(
                "main-server",
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(),
                LocalDateTime.now()
        );
        statsClient.hit(hitRequest);
    }

    private void checkEventUpdatePrivate(Event event, Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не существует");
        }

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException("Пользователь " + userId + " не является создателем события " + eventId);
        }

        if (event.getState() == State.PUBLISHED) {
            throw new ConflictException("Событие не отменено и не в состоянии ожидания.");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Время события указано раньше, чем через два часа от текущего момента");
        }
    }

    private void checkUpdateEventRequestPrivate(Event event, Long userId, Long eventId,
                                                EventRequestStatusUpdateRequest dto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не существует");
        }

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException("Пользователь " + userId + " не является создателем события " + eventId);
        }

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Событие не опубликовано");
        }

        if (event.getConfirmedRequests() != null) {
            if (RequestStatus.CONFIRMED.equals(dto.getStatus())
                    && event.getConfirmedRequests() >= event.getParticipantLimit()) {
                throw new ConflictException("Достигнут лимит заявок");
            }
        }
    }
}
