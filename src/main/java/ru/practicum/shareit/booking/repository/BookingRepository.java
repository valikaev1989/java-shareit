package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Получение списка бронирований пользователя с учетом статуса
     */
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long booker, BookingStatus status);

}