package ru.practicum.stats;

public interface EndpointHitStatsProjection {
    String getApp();

    String getUri();

    Long getHits();
}
