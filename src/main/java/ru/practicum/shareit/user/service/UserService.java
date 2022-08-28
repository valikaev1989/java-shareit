package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

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
     *@return UserDto
     */
    UserDto findUserDtoById(Long userId);

    /**
     * Поиск пользователя по id
     *@return UserDto
     */
    User findUserById(Long userId);

    /**
     * Удаление пользователя по id
     */
    void deleteUser(Long userId);
}