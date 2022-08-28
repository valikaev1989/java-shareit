package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;

import java.util.List;

public interface BookingService {
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
     * Получение списка всех бронирований пользователя
     */
    List<BookingDto> getAllBookingsFromUser(long userId, String state);

    /**
     * Получние списка бронирования для всех предметов пользователя
     */
    List<BookingDto> getBookingByIdOwner(long userId, String state);
}