package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.EndpointHitDtoRequest;
import ru.practicum.stats.EndpointHitStatsProjection;
import ru.practicum.stats.ViewStatDtoResponse;
import ru.practicum.stats.storage.StatsRepository;
import ru.practicum.stats.mapper.EndpointDtoMapper;

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
        statsRepository.save(EndpointDtoMapper.mapDtoToEntity(dto));
    }

    public List<ViewStatDtoResponse> findStats(LocalDateTime start, LocalDateTime end,
                                               List<String> uris, Boolean unique) {

        List<EndpointHitStatsProjection> resultList;

        if (Objects.isNull(uris) || uris.isEmpty()) {

            if (!unique) {
                log.info("Выполнение метода findAllNoUrisFalseUnique");
                resultList = statsRepository.findAllNotUrisFalseUnique(start, end);
            } else {
                log.info("Выполнение метода findAllNoUrisTrueUnique");
                resultList = statsRepository.findAllNotUrisTrueUnique(start, end);
            }

        } else {

            if (!unique) {
                log.info("Выполнение метода findAllYesUrisFalseUnique");
                resultList = statsRepository.findAllWithUrisFalseUnique(start, end, uris);
            } else {
                log.info("Выполнение метода findAllYesUrisTrueUnique");
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
