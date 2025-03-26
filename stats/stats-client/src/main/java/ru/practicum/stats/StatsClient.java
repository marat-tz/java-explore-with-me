package ru.practicum.stats;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StatsClient {

    private final RestTemplate restTemplate;

    public StatsClient(RestTemplate rest) {
        this.restTemplate = rest;
    }

//    public ResponseEntity<Object> stats() {
//        return restTemplate.exchange();
//    }
//
//    public ResponseEntity<Object> hit(UserRequestDto dto) {
//        return restTemplate.exchange();
//    }

}
