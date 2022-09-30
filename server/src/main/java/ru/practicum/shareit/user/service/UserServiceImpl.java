package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.ValidatorServer;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ValidatorServer validator;

    /**
     * Получение списка пользователей
     */
    @Override
    public List<UserDto> getUsers() {
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    /**
     * Добавление нового пользователя
     *
     * @param userDto dto пользователя
     */
    @Override
    public UserDto addNewUser(UserDto userDto) {
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
        User actualUser = validator.validateAndReturnUserByUserId(userId);
        return userMapper.toUserDto(actualUser);
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