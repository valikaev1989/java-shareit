package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getAllUserItems(Long userId);

    Collection<ItemDto> searchItemByNameAndDesc(String text);

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto getItemById(Long itemId);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    void deleteItemById(Long userId, Long itemId);
}