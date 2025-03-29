package ru.practicum.stats.service;


import ru.practicum.stats.EndpointHitDtoRequest;
import ru.practicum.stats.ViewStatDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void create(EndpointHitDtoRequest dto);
    List<ViewStatDtoResponse> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
