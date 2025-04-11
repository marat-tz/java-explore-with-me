package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Value;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.StateAction;

import java.time.LocalDateTime;

@Value
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    @NotBlank
    String annotation;

    Long category;

    @Size(min = 20, max = 7000)
    @NotBlank
    String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String eventDate;

    LocationDto location;

    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration = true;

    StateAction stateAction;

    @Size(min = 3, max = 120)
    @NotBlank
    String title;
}
