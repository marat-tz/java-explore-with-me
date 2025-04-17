package ru.practicum.comment.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentControllerPublic {

    final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDtoResponse findCommentById(@PathVariable Long commentId) {
        return commentService.findCommentById(commentId);
    }

    @GetMapping("/events/{eventId}")
    public List<CommentDtoResponse> findCommentsByEventId(@PathVariable Long eventId) {
        return commentService.findCommentsByEventId(eventId);
    }
}
