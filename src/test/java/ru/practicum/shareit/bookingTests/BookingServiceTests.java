package ru.practicum.shareit.bookingTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validator;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookingServiceTests extends StorageForTests {
    private BookingServiceImpl mockBookingServiceImpl;
    @Autowired
    private BookingMapper bookingMapper;
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private Validator mockValidator;

    @BeforeEach
    void setUp() {
        mockBookingServiceImpl = new BookingServiceImpl(bookingMapper, mockBookingRepository, mockValidator);
    }

    @Test
    void getBookingsByBookerId() {
        User user2 = createUserTwo();

        Item item1 = createItemNullRequest();
        Item item2 = createItemNullRequest2();
        Item item3 = createItemNullRequest3();

        Booking bookingPast = createBooking(user2, item1, 1L);
        bookingPast.setStart(LocalDateTime.now().minusDays(10));
        bookingPast.setEnd(LocalDateTime.now().minusDays(5));
        bookingPast.setStatus(BookingStatus.APPROVED);

        Booking bookingCurrent = createBooking(user2, item2, 2L);
        bookingCurrent.setStart(LocalDateTime.now().minusDays(4));
        bookingCurrent.setEnd(LocalDateTime.now().plusDays(2));
        bookingCurrent.setStatus(BookingStatus.APPROVED);

        Booking bookingFuture = createBooking(user2, item3, 3L);
        bookingFuture.setStart(LocalDateTime.now().plusDays(5));
        bookingFuture.setEnd(LocalDateTime.now().plusDays(10));

        Booking bookingRejected = createBooking(user2, item2, 4L);
        bookingRejected.setStart(LocalDateTime.now());
        bookingRejected.setEnd(LocalDateTime.now().plusDays(10));
        bookingRejected.setStatus(BookingStatus.REJECTED);

        List<BookingDto> expectedAllBookings = bookingMapper
                .toBookingDtoList(List.of(bookingPast, bookingCurrent, bookingFuture, bookingRejected));
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository.findAllByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(bookingPast, bookingCurrent, bookingFuture, bookingRejected));
        List<BookingDto> actualAllBookings = mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "ALL", 0, 5);
        assertEquals(expectedAllBookings, actualAllBookings);

        List<BookingDto> expectedCurrentBookings = bookingMapper
                .toBookingDtoList(List.of(bookingCurrent, bookingRejected));
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findCurrentBookingByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingCurrent, bookingRejected));
        List<BookingDto> actualCurrentBookings = mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "CURRENT", 0, 5);
        assertEquals(expectedCurrentBookings, actualCurrentBookings);

        List<BookingDto> expectedPastBooking = bookingMapper
                .toBookingDtoList(List.of(bookingPast));
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findPastBookingByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingPast));
        List<BookingDto> actualPastBookings = mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "PAST", 0, 5);
        assertEquals(expectedPastBooking, actualPastBookings);

        List<BookingDto> expectedFutureBooking = bookingMapper
                .toBookingDtoList(List.of(bookingFuture));
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findFutureBookingByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingFuture));
        List<BookingDto> actualFutureBookings = mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "FUTURE", 0, 5);
        assertEquals(expectedFutureBooking, actualFutureBookings);

        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findByBookerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(bookingFuture));
        List<BookingDto> actualWaitingBookings = mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "WAITING", 0, 5);
        assertEquals(expectedFutureBooking, actualWaitingBookings);

        List<BookingDto> expectedRejectedBooking = bookingMapper
                .toBookingDtoList(List.of(bookingRejected));
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findByBookerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(bookingRejected));
        List<BookingDto> actualRejectedBookings = mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "REJECTED", 0, 5);
        assertEquals(expectedRejectedBooking, actualRejectedBookings);

        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        ValidationException ex = assertThrows(ValidationException.class, () -> mockBookingServiceImpl.getBookingsByBookerId(user2.getId(), "Unknown state", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", ex.getMessage());
    }

    @Test
    void getBookingsByOwnerId() {
        User user1 = createUser();
        User user2 = createUserTwo();

        Item item1 = createItemNullRequest();
        Item item2 = createItemNullRequest2();
        Item item3 = createItemNullRequest3();

        Booking bookingPast = createBooking(user2, item1, 1L);
        bookingPast.setStart(LocalDateTime.now().minusDays(10));
        bookingPast.setEnd(LocalDateTime.now().minusDays(5));
        bookingPast.setStatus(BookingStatus.APPROVED);

        Booking bookingCurrent = createBooking(user2, item2, 2L);
        bookingCurrent.setStart(LocalDateTime.now().minusDays(4));
        bookingCurrent.setEnd(LocalDateTime.now().plusDays(2));
        bookingCurrent.setStatus(BookingStatus.APPROVED);

        Booking bookingFuture = createBooking(user2, item3, 3L);
        bookingFuture.setStart(LocalDateTime.now().plusDays(5));
        bookingFuture.setEnd(LocalDateTime.now().plusDays(10));

        Booking bookingRejected = createBooking(user2, item2, 4L);
        bookingRejected.setStart(LocalDateTime.now());
        bookingRejected.setEnd(LocalDateTime.now().plusDays(10));
        bookingRejected.setStatus(BookingStatus.REJECTED);

        List<BookingDto> expectedAllBookings = bookingMapper
                .toBookingDtoList(List.of(bookingPast, bookingCurrent, bookingFuture, bookingRejected));
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user1);
        when(mockBookingRepository.findAllByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(bookingPast, bookingCurrent, bookingFuture, bookingRejected));
        List<BookingDto> actualAllBookings = mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "ALL", 0, 5);
        assertEquals(expectedAllBookings, actualAllBookings);

        List<BookingDto> expectedCurrentBookings = bookingMapper
                .toBookingDtoList(List.of(bookingCurrent, bookingRejected));
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user1);
        when(mockBookingRepository
                .findCurrentBookingByItemOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingCurrent, bookingRejected));
        List<BookingDto> actualCurrentBookings = mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "CURRENT", 0, 5);
        assertEquals(expectedCurrentBookings, actualCurrentBookings);

        List<BookingDto> expectedPastBooking = bookingMapper
                .toBookingDtoList(List.of(bookingPast));
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findPastBookingByItemOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingPast));
        List<BookingDto> actualPastBookings = mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "PAST", 0, 5);
        assertEquals(expectedPastBooking, actualPastBookings);

        List<BookingDto> expectedFutureBooking = bookingMapper
                .toBookingDtoList(List.of(bookingFuture));
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findFutureBookingByItemOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingFuture));
        List<BookingDto> actualFutureBookings = mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "FUTURE", 0, 5);
        assertEquals(expectedFutureBooking, actualFutureBookings);

        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findBookingByOwnerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(bookingFuture));
        List<BookingDto> actualWaitingBookings = mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "WAITING", 0, 5);
        assertEquals(expectedFutureBooking, actualWaitingBookings);

        List<BookingDto> expectedRejectedBooking = bookingMapper
                .toBookingDtoList(List.of(bookingRejected));
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findBookingByOwnerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(bookingRejected));
        List<BookingDto> actualRejectedBookings = mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "REJECTED", 0, 5);
        assertEquals(expectedRejectedBooking, actualRejectedBookings);

        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> mockBookingServiceImpl.getBookingsByOwnerId(user1.getId(), "Unknown state", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", ex.getMessage());
    }

    @Test
    void addBooking() {
        User user = createUserTwo();
        Item item = createItemNullRequest();
        Booking booking = createBooking2();
        booking.setBooker(user);
        BookingDtoOnlyId bookingDtoOnlyId = createBookingDtoOnlyId();
        BookingDto expectedBookingDto = createBookingDto2();
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockValidator.validateAndReturnItemByItemId(anyLong())).thenReturn(item);
        when(mockBookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto actualBookingDto = mockBookingServiceImpl.addBooking(user.getId(), bookingDtoOnlyId);
        assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void getBookingById() {
        User user = createUserTwo();
        Booking booking = createBooking2();
        BookingDto expectedBookingDto = createBookingDto2();
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockValidator.validateForGetBooking(any(User.class), anyLong())).thenReturn(booking);
        BookingDto actualBookingDto = mockBookingServiceImpl.getBookingById(user.getId(), booking.getId());
        assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void updateStatusBooking() {
        User user = createUser();
        Booking booking = createBooking2();
        BookingDto expectedBookingDto = createBookingDto2();
        expectedBookingDto.setStatus(BookingStatus.APPROVED);
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockValidator.validateForUpdateBooking(any(User.class), anyLong(), anyBoolean())).thenReturn(booking);
        when(mockBookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto actualBookingDto = mockBookingServiceImpl.updateStatusBooking(user.getId(), booking.getId(), true);
        assertEquals(expectedBookingDto, actualBookingDto);
    }
}