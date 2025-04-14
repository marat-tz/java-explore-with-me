package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class UpdateEventMapper {

    public Event updateEventPrivate(Event event, UpdateEventUserRequest dto,
                             CategoryRepository categoryRepository,
                             LocationRepository locationRepository,
                             LocationMapper locationMapper) {

        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(dto.getCategory().getId()).orElseThrow(() ->
                    new NotFoundException("Категория с id = " + dto.getCategory().getId() + " не найдена.")));
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getEventDate() != null) {
            if (LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    .isBefore(LocalDateTime.now())) {
                throw new ValidationException("Указана недопустимая дата");
            }
            event.setEventDate(LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (dto.getLocation() != null) {
            event.setLocation(locationRepository.save(locationMapper.toEntity(dto.getLocation())));
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            if (dto.getParticipantLimit() < 0) {
                throw new ValidationException("Нельзя установить отрицательное значение лимита");
            }
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getStateAction() == StateAction.CANCEL_REVIEW) {
            event.setState(State.CANCELED);
        }

        return event;
    }

    public Event updateEventAdmin(Event event, UpdateEventAdminRequest dto,
                                    CategoryRepository categoryRepository,
                                    LocationRepository locationRepository,
                                    LocationMapper locationMapper) {

        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(dto.getCategory()).orElseThrow(() ->
                    new NotFoundException("Категория с id = " + dto.getCategory() + " не найдена.")));
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getEventDate() != null) {
            if (LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    .isBefore(LocalDateTime.now())) {
                throw new ValidationException("Указанная дата уже наступила");
            }
            event.setEventDate(LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (dto.getLocation() != null) {
            event.setLocation(locationRepository.save(locationMapper.toEntity(dto.getLocation())));
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getStateAction() != null) {
            if (dto.getStateAction() == StateAction.PUBLISH_EVENT) {

                if (event.getState() == State.PUBLISHED) {
                    throw new ConflictException("Событие уже опубликовано");
                } else if (event.getState() == State.REJECT) {
                    throw new ConflictException("Событие отменено");
                }

                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }

            if (dto.getStateAction() == StateAction.REJECT_EVENT) {

                if (event.getState() == State.PUBLISHED) {
                    throw new ConflictException("Нельзя отменить опубликованное событие");
                }

                event.setState(State.REJECT);
            }
        }

        return event;

    }
}
