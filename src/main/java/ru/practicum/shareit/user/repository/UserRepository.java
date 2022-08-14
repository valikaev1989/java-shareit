package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Map;

public interface UserRepository {
    User createUser(User user);

    void deleteUserById(Long userId);

    User updateUser(User user);

    User getUserById(Long userId);

    Map<Long, User> getAllUsers();
}