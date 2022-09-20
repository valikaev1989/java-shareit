package ru.practicum.shareit.bookingTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTests extends StorageForTests {
    private final TestEntityManager entityManager;
    private final BookingRepository bookingRepository;

    @Test
    void getUserBookings() {
        User user1 = createUserWithoutId();
        entityManager.persist(user1);
        User user2 = createUserTwoWithoutId();
        entityManager.persist(user2);
        Item item1 = createItemWithoutId(user1);
        entityManager.persist(item1);
        Item item2 = createItemWithoutId2(user1);
        entityManager.persist(item2);
        Item item3 = createItemWithoutId3(user1);
        entityManager.persist(item3);
        Booking bookingPast = createBookingWithoutId(user2, item1);
        bookingPast.setStart(LocalDateTime.now().minusDays(10));
        bookingPast.setEnd(LocalDateTime.now().minusDays(5));
        bookingPast.setStatus(BookingStatus.APPROVED);
        entityManager.persist(bookingPast);
        Booking bookingCurrent = createBookingWithoutId(user2, item2);
        bookingCurrent.setStart(LocalDateTime.now().minusDays(4));
        bookingCurrent.setEnd(LocalDateTime.now().plusDays(2));
        bookingCurrent.setStatus(BookingStatus.APPROVED);
        entityManager.persist(bookingCurrent);
        Booking bookingFuture = createBookingWithoutId(user2, item3);
        bookingFuture.setStart(LocalDateTime.now().plusDays(5));
        bookingFuture.setEnd(LocalDateTime.now().plusDays(10));
        entityManager.persist(bookingFuture);
        Booking bookingRejected = createBookingWithoutId(user2, item2);
        bookingRejected.setStart(LocalDateTime.now());
        bookingRejected.setEnd(LocalDateTime.now().plusDays(10));
        bookingRejected.setStatus(BookingStatus.REJECTED);
        entityManager.persist(bookingRejected);
        Pageable pageable = PageRequest.of(0 / 20, 20, Sort.by(Sort.Direction.DESC, "end"));

        List<Booking> expectedAllBookings = Arrays.asList(bookingPast, bookingCurrent, bookingFuture, bookingRejected);
        expectedAllBookings.sort(Comparator.comparing(Booking::getStart).reversed());
        List<Booking> actualAllBookings = bookingRepository.findAllByBookerId(user2.getId(), pageable);
        assertEquals(expectedAllBookings, actualAllBookings);

        List<Booking> expectedCurrentBookings = Arrays.asList(bookingCurrent, bookingRejected);
        expectedCurrentBookings.sort(Comparator.comparing(Booking::getStart).reversed());
        List<Booking> actualCurrentBookings = bookingRepository.findCurrentBookingByBookerId(user2.getId(), LocalDateTime.now(), pageable);
        assertEquals(expectedCurrentBookings, actualCurrentBookings);

        List<Booking> expectedPastBooking = List.of(bookingPast);
        List<Booking> actualPastBookings = bookingRepository.findPastBookingByBookerId(user2.getId(), LocalDateTime.now(), pageable);
        assertEquals(expectedPastBooking, actualPastBookings);

        List<Booking> expectedFutureBooking = List.of(bookingFuture);
        List<Booking> actualFutureBookings = bookingRepository.findFutureBookingByBookerId(user2.getId(), LocalDateTime.now(), pageable);
        assertEquals(expectedFutureBooking, actualFutureBookings);

        List<Booking> actualWaitingBookings = bookingRepository.findByBookerIdAndStatus(user2.getId(), BookingStatus.WAITING, pageable);
        assertEquals(expectedFutureBooking, actualWaitingBookings);

        List<Booking> expectedRejectedBooking = List.of(bookingRejected);
        List<Booking> actualRejectedBookings = bookingRepository.findByBookerIdAndStatus(user2.getId(), BookingStatus.REJECTED, pageable);
        assertEquals(expectedRejectedBooking, actualRejectedBookings);
    }

    @Test
    void getOwnerBookings() {
        User user1 = createUserWithoutId();
        entityManager.persist(user1);
        User user2 = createUserTwoWithoutId();
        entityManager.persist(user2);
        Item item1 = createItemWithoutId(user1);
        entityManager.persist(item1);
        Item item2 = createItemWithoutId2(user1);
        entityManager.persist(item2);
        Item item3 = createItemWithoutId3(user1);
        entityManager.persist(item3);
        Booking bookingPast = createBookingWithoutId(user2, item1);
        bookingPast.setStart(LocalDateTime.now().minusDays(10));
        bookingPast.setEnd(LocalDateTime.now().minusDays(5));
        bookingPast.setStatus(BookingStatus.APPROVED);
        entityManager.persist(bookingPast);
        Booking bookingCurrent = createBookingWithoutId(user2, item2);
        bookingCurrent.setStart(LocalDateTime.now().minusDays(4));
        bookingCurrent.setEnd(LocalDateTime.now().plusDays(2));
        bookingCurrent.setStatus(BookingStatus.APPROVED);
        entityManager.persist(bookingCurrent);
        Booking bookingFuture = createBookingWithoutId(user2, item3);
        bookingFuture.setStart(LocalDateTime.now().plusDays(5));
        bookingFuture.setEnd(LocalDateTime.now().plusDays(10));
        entityManager.persist(bookingFuture);
        Booking bookingRejected = createBookingWithoutId(user2, item2);
        bookingRejected.setStart(LocalDateTime.now());
        bookingRejected.setEnd(LocalDateTime.now().plusDays(10));
        bookingRejected.setStatus(BookingStatus.REJECTED);
        entityManager.persist(bookingRejected);
        Pageable pageable = PageRequest.of(0 / 20, 20, Sort.by(Sort.Direction.DESC, "end"));

        List<Booking> expectedAllBookings = Arrays.asList(bookingPast, bookingCurrent, bookingFuture, bookingRejected);
        expectedAllBookings.sort(Comparator.comparing(Booking::getStart).reversed());
        List<Booking> actualAllBookings = bookingRepository.findAllByItemOwnerId(user1.getId(), pageable);
        assertEquals(expectedAllBookings, actualAllBookings);

        List<Booking> expectedCurrentBookings = Arrays.asList(bookingCurrent, bookingRejected);
        expectedCurrentBookings.sort(Comparator.comparing(Booking::getStart).reversed());
        List<Booking> actualCurrentBookings = bookingRepository.findCurrentBookingByItemOwnerId(user1.getId(), LocalDateTime.now(), pageable);
        assertEquals(expectedCurrentBookings, actualCurrentBookings);

        List<Booking> expectedPastBooking = List.of(bookingPast);
        List<Booking> actualPastBookings = bookingRepository.findPastBookingByItemOwnerId(user1.getId(), LocalDateTime.now(), pageable);
        assertEquals(expectedPastBooking, actualPastBookings);

        List<Booking> expectedFutureBooking = List.of(bookingFuture);
        List<Booking> actualFutureBookings = bookingRepository.findFutureBookingByItemOwnerId(user1.getId(), LocalDateTime.now(), pageable);
        assertEquals(expectedFutureBooking, actualFutureBookings);

        List<Booking> actualWaitingBookings = bookingRepository.findBookingByOwnerIdAndStatus(user1.getId(), BookingStatus.WAITING, pageable);
        assertEquals(expectedFutureBooking, actualWaitingBookings);

        List<Booking> expectedRejectedBooking = List.of(bookingRejected);
        List<Booking> actualRejectedBookings = bookingRepository.findBookingByOwnerIdAndStatus(user1.getId(), BookingStatus.REJECTED, pageable);
        assertEquals(expectedRejectedBooking, actualRejectedBookings);
    }

    @Test
    void getLastNextBooking() {
        User user1 = createUserWithoutId();
        entityManager.persist(user1);
        User user2 = createUserTwoWithoutId();
        entityManager.persist(user2);
        Item item1 = createItemWithoutId(user1);
        entityManager.persist(item1);
        Item item2 = createItemWithoutId2(user1);
        entityManager.persist(item2);
        Item item3 = createItemWithoutId3(user1);
        entityManager.persist(item3);
        Booking bookingPast = createBookingWithoutId(user2, item1);
        bookingPast.setStart(LocalDateTime.now().minusDays(10));
        bookingPast.setEnd(LocalDateTime.now().minusDays(5));
        bookingPast.setStatus(BookingStatus.APPROVED);
        entityManager.persist(bookingPast);
        Booking bookingCurrent = createBookingWithoutId(user2, item1);
        bookingCurrent.setStart(LocalDateTime.now().minusDays(4));
        bookingCurrent.setEnd(LocalDateTime.now().plusDays(2));
        bookingCurrent.setStatus(BookingStatus.APPROVED);
        entityManager.persist(bookingCurrent);
        Booking bookingFuture = createBookingWithoutId(user2, item1);
        bookingFuture.setStart(LocalDateTime.now().plusDays(5));
        bookingFuture.setEnd(LocalDateTime.now().plusDays(10));
        entityManager.persist(bookingFuture);
        Booking bookingRejected = createBookingWithoutId(user2, item1);
        bookingRejected.setStart(LocalDateTime.now());
        bookingRejected.setEnd(LocalDateTime.now().plusDays(7));
        bookingRejected.setStatus(BookingStatus.REJECTED);
        entityManager.persist(bookingRejected);

        Booking lastBooking = bookingRepository.findFirstByItemOrderByStartAsc(item1);
        assertEquals(bookingPast, lastBooking);

        Booking nextBooking = bookingRepository.findFirstByItemOrderByEndDesc(item1);
        assertEquals(bookingFuture, nextBooking);
    }


    @Test
    void validateForTakeItem() {
        User user1 = createUserWithoutId();
        entityManager.persist(user1);
        User user2 = createUserTwoWithoutId();
        entityManager.persist(user2);
        User user3 = createUserThreeWithoutId();
        entityManager.persist(user3);

        Item item1 = createItemWithoutId(user1);
        entityManager.persist(item1);
        Item item2 = createItemWithoutId2(user1);
        entityManager.persist(item2);
        Item item3 = createItemWithoutId3(user1);
        entityManager.persist(item3);

        Booking bookingPast = createBookingWithoutId(user2, item1);
        bookingPast.setStart(LocalDateTime.now().minusDays(10));
        bookingPast.setEnd(LocalDateTime.now().minusDays(5));
        bookingPast.setStatus(BookingStatus.APPROVED);
        entityManager.persist(bookingPast);
        Booking bookingCurrent = createBookingWithoutId(user2, item1);
        bookingCurrent.setStart(LocalDateTime.now().minusDays(4));
        bookingCurrent.setEnd(LocalDateTime.now().plusDays(2));
        bookingCurrent.setStatus(BookingStatus.APPROVED);
        entityManager.persist(bookingCurrent);
        Booking bookingFuture = createBookingWithoutId(user2, item1);
        bookingFuture.setStart(LocalDateTime.now().plusDays(5));
        bookingFuture.setEnd(LocalDateTime.now().plusDays(10));
        entityManager.persist(bookingFuture);
        Booking bookingRejected = createBookingWithoutId(user2, item1);
        bookingRejected.setStart(LocalDateTime.now());
        bookingRejected.setEnd(LocalDateTime.now().plusDays(7));
        bookingRejected.setStatus(BookingStatus.REJECTED);
        entityManager.persist(bookingRejected);

        List<Booking> actualOtherUserBooking = bookingRepository.validateForTakeItem
                (BookingStatus.REJECTED, user3, item1, LocalDateTime.now());
        assertEquals(List.of(), actualOtherUserBooking);

        List<Booking> actualOwnerBooking = bookingRepository.validateForTakeItem
                (BookingStatus.REJECTED, user1, item1, LocalDateTime.now());
        assertEquals(List.of(), actualOwnerBooking);

        List<Booking> expectedBooker = List.of(bookingPast, bookingCurrent);
        List<Booking> actualBooker = bookingRepository.validateForTakeItem
                (BookingStatus.REJECTED, user2, item1, LocalDateTime.now());
        assertEquals(expectedBooker, actualBooker);
    }
}