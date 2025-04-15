package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> findUserRequestsPrivate(Long userId);

    ParticipationRequestDto createUserRequestPrivate(Long userId, Long eventId);

    ParticipationRequestDto cancelUserRequestPrivate(Long userId, Long requestId);

}
