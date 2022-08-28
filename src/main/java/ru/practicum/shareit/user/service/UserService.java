package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    /**
     * Получение списка пользователей
     */
    List<UserDto> getUsers();

    /**
     * Добавление нового пользователя
     */
    UserDto addNewUser(UserDto userDto);

    /**
     * Обновление пользователя
     */
    UserDto updateUser(UserDto userDto, Long userId);

    /**
     * Поиск пользователя по id
     */
    UserDto getUserById(Long userId);

    /**
     * Удаление пользователя по id
     */
    void deleteUser(Long userId);
}