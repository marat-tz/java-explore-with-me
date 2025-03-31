package ru.practicum.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.stats.EndpointHitDtoRequest;
import ru.practicum.stats.model.EndpointHit;

@UtilityClass
public class EndpointDtoMapper {

    public EndpointHit mapDtoToEntity(EndpointHitDtoRequest dto) {
        return EndpointHit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }

}

