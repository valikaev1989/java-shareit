package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItems = new HashMap<>();
    private long itemId = 0;

    private Long generateItemId() {
        return ++itemId;
    }

    /**
     * Получение списка предметов пользователя
     *
     * @param userId id пользователя
     */
    @Override
    public List<Item> findByUserId(Long userId) {
        return userItems.get(userId);
    }

    /**
     * Получение всех предметов
     */
    @Override
    public Map<Long, Item> getAllItem() {
        return items;
    }

    /**
     * Поиск предмета по описанию
     *
     * @param text описание предмета
     */
    @Override
    public List<Item> searchItemByNameAndDesc(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Добавление предмета
     *
     * @param item предмет
     */
    @Override
    public Item addItem(Item item) {
        item.setId(generateItemId());
        items.put(item.getId(), item);
        userItems.compute(item.getOwner().getId(), (userId, itemsList) -> {
            if (itemsList == null) {
                itemsList = new ArrayList<>();
            }
            itemsList.add(item);
            return itemsList;
        });
        return item;
    }

    /**
     * Удаление предмета по id пользователя и id предмета
     *
     * @param userId id пользователя
     * @param itemId id предмета
     */
    @Override
    public void deleteItemById(Long userId, Long itemId) {
        List<Item> userItemsList = userItems.get(userId);
        userItemsList.removeIf(item -> item.getId().equals(itemId));
        items.remove(itemId);
    }

    /**
     * Обновление данных предмета
     *
     * @param item предмет
     */
    @Override
    public Item updateItem(Item item) {
        deleteItemById(item.getOwner().getId(), item.getId());
        userItems.compute(item.getOwner().getId(), (userId, itemsList) -> {
            itemsList.add(item);
            return itemsList;
        });
        items.put(item.getId(), item);
        return item;
    }


    /**
     * Поиск предмета по id
     *
     * @param itemId id предмета
     */
    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }
}