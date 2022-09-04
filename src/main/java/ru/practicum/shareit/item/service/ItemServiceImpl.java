package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private  final BookingMapper bookingMapper;
    private final CommentService commentService;
    private final Validator validator;

    /**
     * Получение предметов пользователя
     *
     * @param userId id пользователя
     */
    @Override
    public List<ItemOwnerDto> getAllUserItems(long userId) {
        List<Item> userItems = itemRepository.findByOwnerIdOrderById(userId);
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
       validator.validateItemAll(itemDto);
        User user = validator.validateAndReturnUserByUserId(userId);
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
    public ItemOwnerDto findItemOwnerDtoById(long userId,long itemId ) {
        log.info("ItemServiceImpl.findItemOwnerDtoById userId {} itemId {}", userId, itemId);
        Item item = validator.validateAndReturnItemByItemId(itemId);
        Booking l = bookingRepository.findFirstByItem_IdOrderByEndDesc(itemId);
        Booking n = bookingRepository.findFirstByItem_IdOrderByStartAsc(itemId);
        BookingDtoOnlyId last = null;
        BookingDtoOnlyId next = null;
        if (l != null) {
            last = bookingMapper.toBookingDtoOnlyId(l);
        }
        if (n != null) {
            next = bookingMapper.toBookingDtoOnlyId(n);
        }
        List<CommentDto> comments = commentService.getCommentsByItemId(itemId);
        if (userId == item.getOwner().getId()) {
            ItemOwnerDto itemOwnerDto = itemMapper.toItemOwnerDto(item, comments, next, last);
            log.info("userId{}==item.getOwner().getId(){}", userId,item.getOwner().getId());
            log.info("ItemServiceImpl.findItemOwnerDtoById return itemOwnerDto{}", itemOwnerDto);
            return itemOwnerDto;
        }
        ItemOwnerDto itemOwnerDto = itemMapper.toItemOwnerDto(item, comments, null, null);
        log.info("userId{}!=item.getOwner().getId(){}", userId,item.getOwner().getId());
        log.info("ItemServiceImpl.findItemOwnerDtoById return itemOwnerDto{}", itemOwnerDto);
        return itemOwnerDto;
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
        Item item = validator.validateAndReturnItemByItemId(itemId);
        validator.validateAndReturnUserByUserId(userId);
        validator.validateOwnerFromItem(userId, itemId);
        if (itemDto.getName() != null) {
            validator.validateItemName(itemDto);
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            validator.validateItemDesc(itemDto);
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
        validator.validateAndReturnUserByUserId(userId);
        validator.validateAndReturnItemByItemId(itemId);
        validator.validateOwnerFromItem(userId, itemId);
        itemRepository.deleteById(itemId);
    }

//    private void validateOwnerFromItem(Long userId, Long itemId) {
//        if (!userId.equals(validateAndReturnItemByItemId(itemId).getOwner().getId())) {
//            log.warn("пользователь с userId '{}' не является владельцем предмета с itemId {}!", userId, itemId);
//            throw new UserNotFoundException(String.format("предмет с userId '%d' не является " +
//                    "владельцем предмета с itemId '%d'!", userId, itemId));
//        }
//    }
//
//    private void validateItemAll(ItemDto itemDto) {
//        validateItemName(itemDto);
//        validateItemDesc(itemDto);
//        if (itemDto.getAvailable() == null) {
//            log.warn("доступность предмета не должна быть пустым!");
//            throw new ValidationException("доступность предмета не должна быть пустым!");
//        }
//    }
//
//    private void validateItemName(ItemDto itemDto) {
//        if (itemDto.getName().isEmpty() || itemDto.getName() == null) {
//            log.warn("имя предмета не должно быть пустым!");
//            throw new ValidationException("имя предмета не должно быть пустым!");
//        }
//    }
//
//    private void validateItemDesc(ItemDto itemDto) {
//        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
//            log.warn("описание предмета не должно быть пустым!");
//            throw new ValidationException("описание предмета не должно быть пустым!");
//        }
//    }
//
//    private Item validateAndReturnItemByItemId(Long itemId) {
//        return itemRepository.findById(itemId).orElseThrow(() ->
//                new ItemNotFoundException(String.format("предмет с id '%d' не найден в списке предметов!",
//                        itemId)));
//    }
}