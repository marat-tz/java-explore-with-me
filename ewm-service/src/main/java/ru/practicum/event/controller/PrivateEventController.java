package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateEventController {

    final EventService eventService;

    @GetMapping
    public List<EventShortDto> findEventsPrivate(@PathVariable Long userId, @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return eventService.findEventsPrivate(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventPrivate(@PathVariable Long userId, @Valid @RequestBody NewEventDto dto) {
        return eventService.createEventPrivate(userId, dto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventByUserPrivate(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.findEventByUserPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventPrivate(@PathVariable Long userId, @PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventUserRequest dto) {
        return eventService.updateEventPrivate(userId, eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findEventRequestsPrivate(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.findEventRequestsPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestPrivate(@PathVariable Long userId, @PathVariable Long eventId,
                                                                    @RequestBody EventRequestStatusUpdateRequest dto) {
        return eventService.updateEventRequestPrivate(userId, eventId, dto);
    }
}

