package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static long id = 0;
    private final static Map<Long, User> users = new HashMap<>();

    private static long generateId() {
        return ++id;
    }

    /**
     * Получение всех пользователей
     */
    @Override
    public Map<Long, User> getAllUsers() {
        return users;
    }

    /**
     * Добавление нового пользователя
     *
     * @param user пользователь
     */
    @Override
    public User createUser(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Удаление пользователя
     *
     * @param userId id пользователя
     */
    @Override
    public void deleteUserById(Long userId) {
        users.remove(userId);
    }

    /**
     * Обновление пользователя
     *
     * @param user пользователь
     */
    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    /**
     * Поиск пользователя
     *
     * @param userId id пользователя
     */
    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }
}