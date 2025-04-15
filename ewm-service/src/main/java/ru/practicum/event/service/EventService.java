package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.State;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventShortDto> findEventsPrivate(Long userId, Integer from, Integer size);

    List<EventShortDto> findEventsPublic(String text, List<Integer> categories,
                                         Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable,
                                         String sort, Integer from, Integer size, HttpServletRequest httpServletRequest);

    List<EventFullDto> findAdminEvents(List<Integer> users, List<State> states,
                                       List<Integer> categories,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Integer from,
                                       Integer size);

    EventFullDto findEventByIdPublic(Long eventId, HttpServletRequest httpServletRequest);

    EventFullDto createEventPrivate(Long userId, NewEventDto dto);

    EventFullDto findEventByUserPrivate(Long userId, Long eventId);

    EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest dto);

    List<ParticipationRequestDto> findEventRequestsPrivate(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestPrivate(Long userId, Long eventId, EventRequestStatusUpdateRequest dto);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest dto);

}
