package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Получение списка пользователей
     */
    @Override
    public List<UserDto> getUsers() {
        return userMapper.toUserDto(userRepository.getAllUsers().values());
    }

    /**
     * Добавление нового пользователя
     *
     * @param userDto dto пользователя
     */
    @Override
    public UserDto addNewUser(UserDto userDto) {
        validateUser(userDto);
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.createUser(user));
    }

    /**
     * Обновление пользователя
     *
     * @param userDto dto пользователя
     */
    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        validateIdUser(userId);
        User user = userRepository.getUserById(userId);
        if (userDto.getName() != null) {
            validateNameUser(userDto);
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateEmailUser(userDto);
            user.setEmail(userDto.getEmail());
        }
        userRepository.updateUser(user);
        return userMapper.toUserDto(user);
    }

    /**
     * Поиск пользователя по id
     *
     * @param userId id пользователя
     */
    @Override
    public UserDto getUserById(Long userId) {
        validateIdUser(userId);
        return userMapper.toUserDto(userRepository.getUserById(userId));
    }

    /**
     * Удаление пользователя по id
     *
     * @param userId id пользователя
     */
    @Override
    public void deleteUser(Long userId) {
        validateIdUser(userId);
        userRepository.deleteUserById(userId);
    }

    private void validateIdUser(Long userId) {
        if (userId == null || !userRepository.getAllUsers().containsKey(userId)) {
            log.warn("пользователь с id '{}' не найден в списке пользователей!", userId);
            throw new UserNotFoundException(String.format("пользователь с id '%d' не найден в списке пользователей!",
                    userId));
        }
    }

    private void validateUser(UserDto userDto) {
        validateNameUser(userDto);
        validateNotNullEmailUser(userDto);
        validateEmailUser(userDto);
    }

    private void validateNameUser(UserDto userDto) {
        if (userDto.getName().isEmpty() || userDto.getName().contains(" ")) {
            log.warn("Логин не должен быть пустым и не должен содержать пробелов");
            throw new ValidationException("некорректный логин");
        }
    }

    private void validateNotNullEmailUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            log.warn("отсутствует адрес электронной почты: {}", userDto.getEmail());
            throw new ValidationException("email отсутствует");
        }
    }

    private void validateEmailUser(UserDto userDto) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+" +
                "@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(userDto.getEmail());
        if (!m.matches()) {
            log.warn("Некорректный адрес электронной почты: {}", userDto.getEmail());
            throw new ValidationException("некорректный email");
        }
        if (userRepository.getAllUsers().values().stream()
                .anyMatch(x -> x.getEmail().equalsIgnoreCase(userDto.getEmail()))) {
            log.warn("Пользователь '{}' с электронной почтой '{}' уже существует.",
                    userDto.getName(), userDto.getEmail());
            throw new AlreadyExistsException("Пользователь с такой электронной почтой уже существует.");
        }
    }
}