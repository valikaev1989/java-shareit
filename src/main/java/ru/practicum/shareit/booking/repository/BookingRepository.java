package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Получение списка бронирований пользователя с учетом статуса
     */
    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2 order by b.start DESC")
    List<Booking> findByBookerIdAndStatus(long bookerId, BookingStatus status);

    /**
     * Получение списка бронирований пользователя
     */
    @Query("select b from Booking b where b.booker.id = ?1 order by b.start DESC")
    List<Booking> findAllByBookerId(long bookerId);

    /**
     * Получение списка будущих бронирований пользователя
     */
    @Query("select b from Booking b where b.booker.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findFutureBookingByBookerId(long bookerId, LocalDateTime now);

    /**
     * Получение списка прошедших бронирований пользователя
     */
    @Query("select b from Booking b where b.booker.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findPastBookingByBookerId(long bookerId, LocalDateTime now);

    /**
     * Получений списка текущих бронирований пользователя
     */
    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start DESC")
    List<Booking> findCurrentBookingByBookerId(long booker, LocalDateTime now);

    /**
     * Получение списка бронирований владельца предметов  с учетом статуса
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.start DESC")
    List<Booking> findBookingByOwnerIdAndStatus(long ownerId, BookingStatus status);

    /**
     * Получение списка бронирований владельца предметов
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start DESC")
    List<Booking> findAllByItemOwnerId(long ownerId);

    /**
     * Получение списка будущих бронирований владельца предметов
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findFutureBookingByItemOwnerId(long ownerId, LocalDateTime now);

    /**
     * Получение списка прошедших бронирований владельца предметов
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findPastBookingByItemOwnerId(long ownerId, LocalDateTime now);

    /**
     * Получение списка текущих бронирований владельца предметов
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start DESC")
    List<Booking> findCurrentBookingByItemOwnerId(long ownerId, LocalDateTime now);

    /**
     * Получение последнего бронирования
     */
    Booking findFirstByItem_IdOrderByEndDesc(long itemId);

    /**
     * Получение следующего бронирования
     */
    Booking findFirstByItem_IdOrderByStartAsc(long itemId);

    /**
     * проверка что пользователь брал вещь в аренду
     */
    @Query("select b from Booking b where b.status <> ?1 and b.booker = ?2 and b.item = ?3 and b.start <= ?4")
    List<Booking> validateForTakeItem(BookingStatus bookingStatus, User user, Item item, LocalDateTime localDateTime);
}