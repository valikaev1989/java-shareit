package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Validator;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Validator validator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, Validator validator) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.validator = validator;
    }

    /**
     * Получение списка пользователей
     */
    @Override
    public List<UserDto> getUsers() {
        return userMapper.toUserDto(userRepository.findAll());
    }

    /**
     * Добавление нового пользователя
     *
     * @param userDto dto пользователя
     */
    @Override
    public UserDto addNewUser(UserDto userDto) {
        validator.validateUserDTO(userDto);
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    /**
     * Обновление пользователя
     *
     * @param userDto dto пользователя
     */
    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = validator.validateAndReturnUserByUserId(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validator.validateEmailUser(userDto);
            user.setEmail(userDto.getEmail());
        }
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    /**
     * Поиск пользователя по id
     *
     * @param userId id пользователя
     * @return UserDto
     */
    @Override
    public UserDto findUserDtoById(Long userId) {
        return userMapper.toUserDto(validator.validateAndReturnUserByUserId(userId));
    }

    /**
     * Удаление пользователя по id
     *
     * @param userId id пользователя
     */
    @Override
    public void deleteUser(Long userId) {
        validator.validateAndReturnUserByUserId(userId);
        userRepository.deleteById(userId);
    }
}