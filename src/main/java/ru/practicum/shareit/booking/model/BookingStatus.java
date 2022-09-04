package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    /**
     * новое бронирование, ожидает одобрения
     */
    WAITING,
    /**
     * бронирование подтверждено владельцем
     */
    APPROVED,
    /**
     * бронирование отклонено владельцем
     */
    REJECTED,
    /**
     * бронирование отменено создателем
     */
    CANCELED
}