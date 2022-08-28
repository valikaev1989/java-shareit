package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Получение списка предметов пользователя
     *
     * @param userId id пользователя
     */
    List<Item> findByOwnerId(long userId);

    /**
     * Поиск предметов по тексту в названии и описании.
     *
     * @param name текст для поиска
     * @param description текст для поиска
     */
    List<Item> searchAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue
            (String name, String description);
}