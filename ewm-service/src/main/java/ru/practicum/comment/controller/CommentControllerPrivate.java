package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentControllerPrivate {

    final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoResponse create(@PathVariable Long userId, @PathVariable Long eventId,
                                     @Valid @RequestBody CommentDtoRequest dto) {
        return commentService.create(userId, eventId, dto);
    }

    @PatchMapping("/{commId}")
    public CommentDtoResponse update(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long commId,
                                     @Valid @RequestBody CommentDtoRequest dto) {
        return commentService.update(userId, eventId, commId, dto);
    }

    @DeleteMapping("/{commId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long commId) {
        commentService.delete(userId, eventId, commId);
    }

    @GetMapping
    public List<CommentDtoResponse> findCommentsByUserIdAndEventId(@PathVariable Long userId,
                                                                   @PathVariable Long eventId) {
        return commentService.findCommentsByUserIdAndEventId(userId, eventId);
    }

}
