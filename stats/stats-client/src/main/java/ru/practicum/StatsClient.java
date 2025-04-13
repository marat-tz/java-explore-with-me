package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class StatsClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplate rest) {
        this.restTemplate = rest;
        this.serverUrl = serverUrl;
    }

    public List<ViewStatDtoResponse> findStats(LocalDateTime start, LocalDateTime end,
                                               List<String> uris, Boolean unique) {
        log.info("Начало метода findStats");
        String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .toUriString();

        log.info("Получение ResponseEntity findStats");
        ResponseEntity<List<ViewStatDtoResponse>> response = restTemplate.exchange(uri, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    public void hit(EndpointHitDtoRequest dto) {
        String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/hit")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDtoRequest> entity = new HttpEntity<>(dto, headers);

        restTemplate.exchange(uri, HttpMethod.POST, entity, Void.class);
    }
}
