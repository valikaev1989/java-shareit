package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final CommentService commentService;

    @Autowired
    public ItemServiceImpl(ItemMapper itemMapper, ItemRepository itemRepository,
                           UserService userService,  BookingService bookingService, CommentService commentService) {
        this.itemMapper = itemMapper;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingService = bookingService;
        this.commentService = commentService;
    }

    /**
     * Получение предметов пользователя
     *
     * @param userId id пользователя
     */
    @Override
    public List<ItemOwnerDto> getAllUserItems(long userId) {
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        List<ItemOwnerDto> result = new ArrayList<>();
        for (Item item : userItems) {
            result.add(findItemOwnerDtoById(userId, item.getId()));
        }
        return result;
    }

    /**
     * Поиск предмета по тексту в названии или описании
     *
     * @param text текст для поиска
     */
    @Override
    public List<ItemDto> searchItemByNameAndDesc(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemMapper.toItemDto(itemRepository.
                searchAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text));
    }

    /**
     * Добавление предмета
     *
     * @param userId  id пользователя
     * @param itemDto dto предмета
     */
    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        validateItemAll(itemDto);
        User user = userService.findUserById(userId);
        Item item = itemRepository.save(itemMapper.toItem(itemDto, user));
        return itemMapper.toItemDto(item);
    }

    /**
     * Поиск предмета по id
     *
     * @param itemId id предмета
     * @param userId id пользователя
     */
    @Override
    public ItemOwnerDto findItemOwnerDtoById(long itemId, long userId) {
        Item item = validateAndReturnItemByItemId(itemId);
        BookingDto lastBooking = bookingService.findLastBookingForItem(itemId);
        BookingDto nextBooking = bookingService.findNextBookingForItem(itemId);
        BookingDto last = null;
        BookingDto next = null;
        if (lastBooking != null) {
            last = lastBooking;
        }
        if (nextBooking != null) {
            next = nextBooking;
        }
        List<CommentDto> comments = commentService.getCommentsByItemId(itemId);
        if (userId == item.getOwner().getId()) {
            return itemMapper.toItemOwnerDto(item, comments, next, last);
        }
        return itemMapper.toItemOwnerDto(item, comments, null, null);
    }


    /**
     * Поиск предмета по id для внутреннего пользования в сервисах
     *
     * @param itemId id предмета
     */
    @Override
    public Item findItemById(long itemId) {
        return validateAndReturnItemByItemId(itemId);
    }

    /**
     * Редактирование предмета
     *
     * @param userId  id пользователя
     * @param itemId  id предмета
     * @param itemDto dto предмета
     */
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = validateAndReturnItemByItemId(itemId);
        userService.findUserById(userId);
        validateOwnerFromItem(userId, itemId);
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
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    /**
     * Удаление предмета
     *
     * @param userId id пользователя
     * @param itemId id предмета
     */
    @Override
    public void deleteItemById(long userId, long itemId) {
        userService.findUserById(userId);
        validateAndReturnItemByItemId(itemId);
        validateOwnerFromItem(userId, itemId);
        itemRepository.deleteById(itemId);
    }

    private void validateOwnerFromItem(Long userId, Long itemId) {
        if (!userId.equals(validateAndReturnItemByItemId(itemId).getOwner().getId())) {
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

    private Item validateAndReturnItemByItemId(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("предмет с id '%d' не найден в списке предметов!",
                        itemId)));
    }
}