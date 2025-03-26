package ru.practicum.stats;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.dto.UserRequestDto;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsClient statsClient;

    // Получение статистики по посещениям. Обратите внимание:
    // значение даты и времени нужно закодировать (например используя java.net.URLEncoder.encode)
    // /stats?start=2020-05-05 00:00:00&end=2035-05-05 00:00:00&unique=false
    @GetMapping("/stats")
    public ResponseEntity<Object> findStats(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end,
                                            @RequestParam Boolean unique) {
        return statsClient.findStats();
    }


    // Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
    // Название сервиса, uri и ip пользователя указаны в теле запроса.
    @PostMapping("/hit")
    public ResponseEntity<Object> saveUserRequest(@Valid @RequestBody UserRequestDto dto) {
        return statsClient.saveUserRequest(dto);
    }

}
