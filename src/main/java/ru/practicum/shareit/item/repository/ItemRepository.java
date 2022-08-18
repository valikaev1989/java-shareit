package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemRepository {
    Item addItem(Item item);

    void deleteItemById(Long userId, Long itemId);

    Item updateItem(Item item);

    Map<Long, Item> getAllItem();

    Item getItemById(Long itemId);

    List<Item> searchItemByNameAndDesc(String text);

    List<Item> findByUserId(Long userId);
}