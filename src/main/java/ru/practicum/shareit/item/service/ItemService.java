package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.List;

public interface ItemService {
    /**
     * Получение списка предметов пользователя
     */
    List<ItemOwnerDto> getAllUserItems(long userId);

    /**
     * Поиск предмета по тексту
     */
    List<ItemDto> findItemsByText(String text);

    /**
     * Добавление предмета
     */
    ItemDto addItem(long userId, ItemDto itemDto);

    /**
     * Поиск предмета по id
     *
     * @return ItemOwnerDto предмета
     */
    ItemOwnerDto findItemOwnerDtoById(long userId, long itemId);

    /**
     * Изменение предмета
     */
    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    /**
     * Удаление предмета
     */
    void deleteItemById(long userId, long itemId);
}