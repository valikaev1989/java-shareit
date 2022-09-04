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
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    /**
     * Получение списка бронирований пользователя
     */
    @Query("select b from Booking b where b.booker.id = ?1 order by b.start DESC")
    List<Booking> findByBookerIdOrderByStartDesc(long bookerId);

    /**
     * Получение списка будущих бронирований пользователя
     */
    @Query("select b from Booking b where b.booker.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    /**
     * Получение списка прошедших бронирований пользователя
     */
    @Query("select b from Booking b where b.booker.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime end);

    /**
     * Получений списка текущих бронирований пользователя
     */
    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long bookerId, LocalDateTime end);
    @Query("select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start DESC")
    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long booker, LocalDateTime now);
    /**
     * Получение списка бронирований владельца предметов  с учетом статуса
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.start DESC")
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    /**
     * Получение списка бронирований владельца предметов
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start DESC")
    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId);

    /**
     * Получение списка будущих бронирований владельца предметов
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime localDateTime);

    /**
     * Получение списка прошедших бронирований владельца предметов
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime localDateTime);

    /**
     * Получение списка текущих бронирований владельца предметов
     */
    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?3 order by b.start DESC")
    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long ownerId, LocalDateTime start, LocalDateTime end);

    /**
     * Получение последнего бронирования
     */
    Booking findFirstByItem_IdOrderByEndDesc(long itemId);

    @Query(value = "select * from bookings b " +
            "where b.item_id = :itemId and b.start_date_time < :date order by b.start_date_time limit  1",
            nativeQuery = true)
    Booking findBookingByItemWithDateBefore(long itemId, LocalDateTime date);

    /**
     * Получение следующего бронирования
     */
    Booking findFirstByItem_IdOrderByStartAsc(long itemId);

    @Query(value = "select * from bookings b " +
            "where b.item_id = :itemId and b.start_date_time > :date order by b.start_date_time limit  1",
            nativeQuery = true)
    Booking findBookingByItemWithDateAfter(long itemId, LocalDateTime date);

    /**
     * проверка что пользователь брал вещь в аренду
     */
    @Query("select b from Booking b where b.item = ?1 and b.booker = ?2 and b.status <> ?3")
    List<Booking> findByItemAndBookerAndStatusNot(Item item, User booker, BookingStatus status);

    @Query("select b from Booking b where b.status <> ?1 and b.booker = ?2 and b.item = ?3 and b.start <= ?4")
    List<Booking> findByStatusNotAndBookerAndItemAndStartLessThanEqual
            (BookingStatus bookingStatus, User user, Item item, LocalDateTime localDateTime);
}