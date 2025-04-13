package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.EndpointHitDtoRequest;
import ru.practicum.model.EndpointHit;

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

