package ru.practicum;

public interface EndpointHitStatsProjection {
    String getApp();

    String getUri();

    Long getHits();
}
