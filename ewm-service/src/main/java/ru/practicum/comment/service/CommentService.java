package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;

public interface CommentService {
    CommentDtoResponse create(Long userId, Long eventId, CommentDtoRequest dto);
    CommentDtoResponse update(Long userId, Long eventId, Long commId, CommentDtoRequest dto);
    CommentDtoResponse findCommentById(Long commentId);
}
