package ru.practicum.stats.dto;

import java.time.LocalDateTime;

public class UserRequestDto {
    //  "app": "ewm-main-service",
    //  "uri": "/events/1",
    //  "ip": "192.163.0.1",
    //  "timestamp": "2022-09-06 11:00:23"
    String app;
    String uri;
    String ip;
    LocalDateTime timestamp;
}
