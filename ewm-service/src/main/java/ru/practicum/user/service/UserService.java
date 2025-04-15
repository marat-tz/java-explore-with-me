package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findUsers(List<Integer> ids, Integer from, Integer size);

    UserDto createUser(UserDto dto);

    void deleteUser(Long userId);
}
