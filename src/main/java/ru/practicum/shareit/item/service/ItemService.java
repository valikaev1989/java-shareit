package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    /**
     * Получение списка предметов пользователя
     */
    List<ItemOwnerDto> getAllUserItems(Long userId);

    /**
     * Поиск предмета по описанию
     */
    List<ItemDto> searchItemByNameAndDesc(String text);

    /**
     * Получение списка отзывов предмета
     */
    List<CommentDto> getComments(long itemId);

    /**
     * Добавление предмета
     */
    ItemDto addItem(Long userId, ItemDto itemDto);

    /**
     * Поиск предмета по id
     *
     * @return ItemDto предмета
     */
    ItemDto findItemDtoById(Long itemId);

    /**
     * Поиск предмета по id
     *
     * @return Item сущность предмета
     */
    Item findItemById(Long itemId);

    /**
     * Редактирование предмета
     */
    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    /**
     * Удаление предмета
     */
    void deleteItemById(Long userId, Long itemId);
}