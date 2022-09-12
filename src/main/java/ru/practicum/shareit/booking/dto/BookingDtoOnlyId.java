package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

/**
 * id — уникальный идентификатор бронирования;
 * start — дата начала бронирования;
 * end — дата конца бронирования;
 * itemId — id вещи, которую пользователь бронирует;
 * bookerId — id пользователя, который осуществляет бронирование;
 * status — статус бронирования.
 */
@Data
public class BookingDtoOnlyId {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;
}