package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Value;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.StateAction;

import java.time.LocalDateTime;

@Value
public class UpdateEventUserRequest {

    String annotation;

    CategoryDto category;

    String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    LocationDto location;

    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration = true;

    StateAction stateAction;

    String title;
}
