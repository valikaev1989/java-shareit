package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserMapper userMapper;
    private final UserService userService;


    @Autowired
    public ItemServiceImpl(ItemMapper itemMapper, ItemRepository itemRepository,
                           UserMapper userMapper, UserService userService) {
        this.itemMapper = itemMapper;
        this.itemRepository = itemRepository;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    /**
     * Получение предметов пользователя
     *
     * @param userId id пользователя
     */
    @Override
    public Collection<ItemDto> getAllUserItems(Long userId) {
        userService.getUserById(userId);
        return itemMapper.toItemDto(itemRepository.findByUserId(userId));
    }

    /**
     * Поиск предмета по фрагменту в названии или описании
     *
     * @param text текст для поиска
     */
    @Override
    public Collection<ItemDto> searchItemByNameAndDesc(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemMapper.toItemDto(itemRepository.searchItemByNameAndDesc(text));
    }

    /**
     * Добавление предмета
     *
     * @param userId  id пользователя
     * @param itemDto dto предмета
     */
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        validateItemAll(itemDto);
        User user = userMapper.toUser(userService.getUserById(userId));
        user.setId(userId);
        log.info("ItemServiceImpl addItem user {}", user);
        Item item = itemRepository.addItem(itemMapper.toItem(itemDto, user));
        log.info("ItemServiceImpl addItem item {}", item);
        return itemMapper.toItemDto(item);
    }

    /**
     * Поиск предмета по id
     *
     * @param itemId id предмета
     */
    @Override
    public ItemDto getItemById(Long itemId) {
        validateItemId(itemId);
        return itemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    /**
     * Редактирование предмета
     *
     * @param userId  id пользователя
     * @param itemId  id предмета
     * @param itemDto dto предмета
     */
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        validateItemId(itemId);
        Item item = itemRepository.getItemById(itemId);
        userService.getUserById(userId);
        validateUserFromItem(userId, itemId);
        if (itemDto.getName() != null) {
            validateItemName(itemDto);
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            validateItemDesc(itemDto);
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.updateItem(item));
    }

    /**
     * Удаление предмета
     *
     * @param userId id пользователя
     * @param itemId id предмета
     */
    @Override
    public void deleteItemById(Long userId, Long itemId) {
        validateItemId(itemId);
        userService.getUserById(userId);
        itemRepository.deleteItemById(userId, itemId);
    }

    private void validateUserFromItem(Long userId, Long itemId) {
        if (!userId.equals(itemRepository.getItemById(itemId).getOwner().getId())) {
            log.warn("пользователь с userId '{}' не является владельцем предмета с itemId {}!", userId, itemId);
            throw new UserNotFoundException(String.format("предмет с userId '%d' не является " +
                    "владельцем предмета с itemId '%d'!", userId, itemId));
        }
    }

    private void validateItemAll(ItemDto itemDto) {
        validateItemName(itemDto);
        validateItemDesc(itemDto);
        if (itemDto.getAvailable() == null) {
            log.warn("доступность предмета не должна быть пустым!");
            throw new ValidationException("доступность предмета не должна быть пустым!");
        }
    }

    private void validateItemName(ItemDto itemDto) {
        if (itemDto.getName().isEmpty() || itemDto.getName() == null) {
            log.warn("имя предмета не должно быть пустым!");
            throw new ValidationException("имя предмета не должно быть пустым!");
        }
    }

    private void validateItemDesc(ItemDto itemDto) {
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            log.warn("описание предмета не должно быть пустым!");
            throw new ValidationException("описание предмета не должно быть пустым!");
        }
    }

    private void validateItemId(Long itemId) {
        if (itemId == null || !itemRepository.getAllItem().containsKey(itemId)) {
            log.warn("предмет с id '{}' не найден в списке предметов!", itemId);
            throw new ItemNotFoundException(String.format("предмет с id '%d' не найден в списке предметов!",
                    itemId));
        }
    }
}