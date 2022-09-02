package ru.practicum.shareit.booking.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private static final LocalDateTime TIME_NOW = LocalDateTime.now();

    @Autowired
    public BookingServiceImpl(UserService userService,@Lazy ItemService itemService,
                              BookingMapper bookingMapper, BookingRepository bookingRepository) {
        this.userService = userService;
        this.itemService = itemService;
        this.bookingMapper = bookingMapper;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Получение списка всех бронирований пользователя.
     *
     * @param userId id пользователя
     * @param state  состояние бронирования
     */
    @Override
    public List<BookingDto> getAllBookingsByBooker(long userId, String state) {
        userService.findUserById(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ("CURRENT"):
                bookings.addAll(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc
                        (userId, TIME_NOW, TIME_NOW));
                break;
            case ("FUTURE"):
                bookings.addAll(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, TIME_NOW));
                break;
            case ("PAST"):
                bookings.addAll(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, TIME_NOW));
                break;
            case ("WAITING"):
                bookings.addAll(bookingRepository.findByBookerIdAndStatusOrderByStartDesc
                        (userId, BookingStatus.WAITING));
                break;
            case ("REJECTED"):
                bookings.addAll(bookingRepository.findByBookerIdAndStatusOrderByStartDesc
                        (userId, BookingStatus.REJECTED));
                break;
            default:
                bookings.addAll(bookingRepository.findByBookerIdOrderByStartDesc(userId));
                break;
        }
        return bookingMapper.toBookingDto(bookings);
    }

    /**
     * Получние списка бронирования для всех предметов пользователя
     *
     * @param ownerId id пользователя
     * @param state   состояние бронирования
     */
    @Override
    public List<BookingDto> getBookingByIdOwner(long ownerId, String state) {
        userService.findUserById(ownerId);
        List<Booking> list = new ArrayList<>();
        switch (state) {
            case ("CURRENT"):
                list.addAll(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc
                        (ownerId, TIME_NOW, TIME_NOW));
                break;
            case ("FUTURE"):
                list.addAll(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, TIME_NOW));
                break;
            case ("PAST"):
                list.addAll(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, TIME_NOW));
                break;
            case ("WAITING"):
                list.addAll(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc
                        (ownerId, BookingStatus.WAITING));
                break;
            case ("REJECTED"):
                list.addAll(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc
                        (ownerId, BookingStatus.REJECTED));
                break;
            default:
                list.addAll(bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId));
                break;
        }
        return bookingMapper.toBookingDto(list);
    }

    /**
     * Добавление нового бронирования
     *
     * @param userId     id пользователя
     * @param bookingDto dto бронирования
     */
    @Override
    public BookingDto addBooking(long userId, BookingDtoOnlyId bookingDto) {
        User user = userService.findUserById(userId);
        Item item = itemService.findItemById(bookingDto.getItemId());
        validateForAddBooking(user, item, bookingDto);
        Booking booking = bookingRepository.save(
                bookingMapper.newBooking(bookingDto, user, item, BookingStatus.WAITING));
        return bookingMapper.toBookingDto(booking);
    }

    /**
     * Поиск бронирования по id
     *
     * @param userId    id пользователя
     * @param bookingId id бронирования
     */
    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        User user = userService.findUserById(userId);
        return bookingMapper.toBookingDto(validateForGetBooking(user, bookingId));
    }

    /**
     * Обновление бронирования
     *
     * @param userId    id пользователя
     * @param bookingId id бронирования
     * @param approved  подтверждено бронирование или отклонено
     */
    @Override
    public BookingDto updateStatusBooking(long userId, long bookingId, Boolean approved) {
        User owner = userService.findUserById(userId);
        Booking booking = validateForUpdateBooking(owner, bookingId, approved);
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public void validateBookingForComment(Item item, User booker, BookingStatus status) {
        List<Booking> bookingList = bookingRepository.findByItemAndBookerAndStatusNot(item, booker, status);
        if (bookingList.isEmpty()) {
            log.warn("Вы не брали в аренду эту вещь");
            throw new ValidationException("Вы не брали в аренду эту вещь");
        }
    }

    /**
     * Получение следующего бронирования
     */
    @Override
    public BookingDto findNextBookingForItem(long itemId) {
        return bookingMapper.toBookingDto(bookingRepository.findFirstByItem_IdOrderByStartAsc(itemId));
    }

    /**
     * Получение последнего бронирования
     */
    @Override
    public BookingDto findLastBookingForItem(long itemId) {
        return bookingMapper.toBookingDto(bookingRepository.findFirstByItem_IdOrderByEndDesc(itemId));
    }

    private void validateForAddBooking(User user, Item item, BookingDtoOnlyId bookingDtoId) {
        if (user.equals(item.getOwner())) {
            log.warn("Владелец не может арендовать у себя");
            throw new ItemNotFoundException("Владелец не может арендовать у себя");
        }
        if (!item.getAvailable()) {
            log.warn("Вешь занята");
            throw new ValidationException("Вешь занята");
        }
        LocalDateTime startTime = bookingDtoId.getStart();
        LocalDateTime endTime = bookingDtoId.getEnd();
        if (endTime.isBefore(LocalDateTime.now())) {
            log.warn("Время окончания не корректно");
            throw new ValidationException("Время окончания не корректно");
        }
        if (startTime.isAfter(endTime)) {
            log.warn("Время окончания раньше начала");
            throw new ValidationException("Время окончания раньше начала");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            log.warn("Время начала не корректно");
            throw new ValidationException("Время начала не корректно");
        }
    }

    private Booking validateForGetBooking(User user, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ItemNotFoundException(String.format("бронь предмета с bookingId '%d' не найдена!", bookingId)));
        if (user.equals(booking.getBooker()) || user.equals(booking.getItem().getOwner())) {
            return booking;
        } else {
            log.warn("Вы не владелец или пользователь вещи");
            throw new ItemNotFoundException("Вы не владелец или пользователь вещи");
        }
    }

    private Booking validateForUpdateBooking(User owner, long bookingId, Boolean approved) {
        Booking booking = validateForGetBooking(owner, bookingId);
        if (approved == null) {
            log.warn("Approved не может быть пустым");
            throw new ValidationException("Approved не может быть пустым");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            log.warn("Статус предмета уже изменен(APPROVED)");
            throw new ValidationException("Статус предмета уже изменен(APPROVED)");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.warn("Бронь уже подтверждена(WAITING)");
            throw new ValidationException("Бронь уже подтверждена(WAITING)");
        }
        if (!booking.getItem().getOwner().equals(owner)) {
            log.warn("Вы не владелец вещи");
            throw new UserNotFoundException("Вы не владелец вещи");
        }
        return booking;
    }
}