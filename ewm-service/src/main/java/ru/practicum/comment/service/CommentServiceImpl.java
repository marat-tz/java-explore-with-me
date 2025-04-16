package ru.practicum.comment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentServiceImpl implements CommentService {

    final CommentRepository commentRepository;
    final EventRepository eventRepository;
    final UserRepository userRepository;

    final CommentMapper commentMapper;

    @Override
    public CommentDtoResponse create(Long userId, Long eventId, CommentDtoRequest dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие " + eventId + " не найдено"));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь " + userId + " не найден"));

        Comment comment = commentRepository.save(commentMapper.toEntity(dto, event, user, LocalDateTime.now()));
        return commentMapper.toDto(comment);
    }

    @Override
    public CommentDtoResponse update(Long userId, Long eventId, Long commId, CommentDtoRequest dto) {
        Comment comment = getCommentCheckParams(userId, eventId, commId);
        comment.setText(dto.getText());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public void delete(Long userId, Long eventId, Long commId) {
        getCommentCheckParams(userId, eventId, commId);
        commentRepository.deleteById(commId);
    }

    @Override
    public CommentDtoResponse findCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий " + commentId + " не найден"));
        return commentMapper.toDto(comment);
    }

    @Override
    public List<CommentDtoResponse> findCommentsByEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие " + eventId + " не найдено");
        }

        List<Comment> comments = commentRepository.findAllByEventId(eventId);
        return commentMapper.toDto(comments);
    }

    private Comment getCommentCheckParams(Long userId, Long eventId, Long commId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие " + eventId + " не найдено");
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }

        Comment comment = commentRepository.findById(commId).orElseThrow(() ->
                new NotFoundException("Комментарий " + commId + " не существует"));

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new ConflictException("Пользователь " + userId + " не является создателем комментария");
        }

        return comment;
    }

}
