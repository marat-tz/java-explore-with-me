package ru.practicum.stats;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.exception.InternalErrorException;
import ru.practicum.stats.exception.NotFoundException;
import ru.practicum.stats.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplate rest) {
        this.restTemplate = rest;
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<List<ViewStatDtoResponse>> findStats(LocalDateTime start, LocalDateTime end,
                                                               List<String> uris, Boolean unique) {
        String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .toUriString();

        return restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
    }

    public void hit(EndpointHitDtoRequest dto) {
        String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/hit")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDtoRequest> entity = new HttpEntity<>(dto, headers);

        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.POST, entity, Void.class);

        if (response.getStatusCode().value() == 404) {
            throw new NotFoundException("Ошибка при записи события (метод hit)");

        } else if (response.getStatusCode().value() == 400) {
            throw new ValidationException("Ошибка при записи события(метод hit)");

        } else if (response.getStatusCode().is5xxServerError()) {
            throw new InternalErrorException("Ошибка при записи события(метод hit)");
        }

    }
}
