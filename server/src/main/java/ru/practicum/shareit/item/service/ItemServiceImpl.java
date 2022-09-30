package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ValidatorServer;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentService commentService;
    private final ValidatorServer validator;

    /**
     * Получение предметов пользователя
     *
     * @param userId id пользователя
     * @param from   количество объектов на странице
     * @param size   количество страниц
     */
    @Override
    public List<ItemOwnerDto> getAllUserItems(long userId, int from, int size) {
        validator.validateAndReturnUserByUserId(userId);
//        validator.validatePage(from, size);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").ascending());
        List<Item> userItems = itemRepository.findByOwnerIdOrderById(userId, pageable);
        return userItems.stream().map(item -> findItemOwnerDtoById(userId, item.getId())).collect(Collectors.toList());
    }

    /**
     * Поиск предмета по тексту в названии или описании
     *
     * @param text текст для поиска
     * @param from количество объектов на странице
     * @param size количество страниц
     */
    @Override
    public List<ItemDto> findItemsByText(String text, int from, int size) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
//        validator.validatePage(from, size);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").ascending());
        return itemMapper.toItemDtoList(itemRepository.searchItemByNameAndDesc(text, pageable));
    }

    /**
     * Добавление предмета
     *
     * @param userId  id пользователя
     * @param itemDto dto предмета
     */
    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
//        validator.validateItemAll(itemDto);
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
    public ItemOwnerDto findItemOwnerDtoById(long userId, long itemId) {
        User user = validator.validateAndReturnUserByUserId(userId);
        Item item = validator.validateAndReturnItemByItemId(itemId);
        Booking lastBooking = bookingRepository.findFirstByItemOrderByStartAsc(item);
        Booking nextBooking = bookingRepository.findFirstByItemOrderByEndDesc(item);
        BookingDtoOnlyId lastBookingDto = null;
        BookingDtoOnlyId nextBookingDto = null;
        if (lastBooking != null) {
            lastBookingDto = bookingMapper.toBookingDtoOnlyId(lastBooking);
        }
        if (nextBooking != null) {
            nextBookingDto = bookingMapper.toBookingDtoOnlyId(nextBooking);
        }
        List<CommentDto> comments = commentService.getCommentsByItemId(itemId);
        if (user.getId() == (item.getOwner().getId())) {
            return itemMapper.toItemOwnerDto(item, comments, lastBookingDto, nextBookingDto);
        }
        return itemMapper.toItemOwnerDto(item, comments, null, null);
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
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
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
}