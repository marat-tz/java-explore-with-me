package ru.practicum.request.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> findUserRequestsPrivate(@PathVariable Long userId) {
        return requestService.findUserRequestsPrivate(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createUserRequestPrivate(@PathVariable Long userId,
                                                            @RequestParam(required = false) Long eventId) {
        if (eventId == null) {
            throw new ValidationException("Параметр 'eventId' не указан");
        }
        return requestService.createUserRequestPrivate(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequestPrivate(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelUserRequestPrivate(userId, requestId);
    }

}
