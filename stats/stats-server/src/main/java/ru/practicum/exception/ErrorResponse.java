package ru.practicum.exception;

import lombok.Value;

@Value
public class ErrorResponse {
    String error;
    String description;
}
