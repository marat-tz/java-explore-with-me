package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDtoRequest;
import ru.practicum.EndpointHitStatsProjection;
import ru.practicum.ViewStatDtoResponse;
import ru.practicum.exception.ValidationException;
import ru.practicum.storage.StatsRepository;
import ru.practicum.mapper.EndpointDtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Transactional
    public void create(EndpointHitDtoRequest dto) {
        log.info("Сохранение статистики StatsServiceImpl - create");
        statsRepository.save(EndpointDtoMapper.mapDtoToEntity(dto));
    }

    public List<ViewStatDtoResponse> findStats(LocalDateTime start, LocalDateTime end,
                                               List<String> uris, Boolean unique) {

        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала не может быть позже даты окончания");
        }

        List<EndpointHitStatsProjection> resultList;

        if (Objects.isNull(uris) || uris.isEmpty()) {

            if (!unique) {
                resultList = statsRepository.findAllNotUrisFalseUnique(start, end);
            } else {
                resultList = statsRepository.findAllNotUrisTrueUnique(start, end);
            }

        } else {

            if (!unique) {
                resultList = statsRepository.findAllWithUrisFalseUnique(start, end, uris);
            } else {
                resultList = statsRepository.findAllWithUrisTrueUnique(start, end, uris);
            }

        }

        return resultList.stream()
                .map(stat -> ViewStatDtoResponse.builder()
                        .app(stat.getApp())
                        .uri(stat.getUri())
                        .hits(stat.getHits())
                        .build())
                .toList();
    }
}
