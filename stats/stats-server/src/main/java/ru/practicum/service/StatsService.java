package ru.practicum.service;


import ru.practicum.EndpointHitDtoRequest;
import ru.practicum.ViewStatDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void create(EndpointHitDtoRequest dto);

    List<ViewStatDtoResponse> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
