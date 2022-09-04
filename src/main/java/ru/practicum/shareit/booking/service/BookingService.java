package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingService {
    /**
     * Получение списка всех бронирований пользователя
     */
    List<BookingDto> getAllBookingsByBookerId(long userId, String state);

    /**
     * Получние списка бронирования для всех предметов пользователя
     */
    List<BookingDto> getBookingByOwnerId(long userId, String state);

    /**
     * Добавление нового бронирования
     */
    BookingDto addBooking(long userId, BookingDtoOnlyId bookingDto);

    /**
     * Обновление статуса бронирования
     */
    BookingDto updateStatusBooking(long userId, long bookingId, Boolean approved);

    /**
     * Поиск бронирования
     */
    BookingDto getBookingById(long userId, long bookingId);

    /**
     * проверка что пользователь брал вещь в аренду
     */
//    void validateBookingForComment(Item item, User booker, BookingStatus status);



}