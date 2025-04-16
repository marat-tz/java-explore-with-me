package ru.practicum.comment.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;

import java.util.List;

public interface CommentService {
    CommentDtoResponse create(Long userId, Long eventId, CommentDtoRequest dto);
    CommentDtoResponse update(Long userId, Long eventId, Long commId, CommentDtoRequest dto);
    void delete(Long userId, Long eventId, Long commId);
    CommentDtoResponse findCommentById(Long commentId);
    List<CommentDtoResponse> findCommentsByEventId(Long eventId);
}
