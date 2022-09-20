package ru.practicum.shareit.bookingTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private final User user1 = createUser();
    private final User user2 = createUserTwo();
    private final Item item1 = createItemNullRequest();
    private final Item item2 = createItemNullRequest2();
    private final Item item3 = createItemNullRequest3();
    private final Booking bookingPast = createBooking(user2, item1, 1L);
    private final Booking bookingCurrent = createBooking(user2, item2, 2L);
    private final Booking bookingFuture = createBooking(user2, item3, 3L);
    private final Booking bookingRejected = createBooking(user2, item2, 4L);

    @BeforeEach
    void setUp() {
        mockBookingServiceImpl = new BookingServiceImpl(bookingMapper, mockBookingRepository, mockValidator);
    }

    @Test
    @DisplayName("ServiceMVC Тест ошибки получения букинга пользователя предмета")
    void UnknownStateBookingByBooker() {
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        ValidationException ex = assertThrows(ValidationException.class, () -> mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "Unknown state", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", ex.getMessage());
    }

    @Test
    @DisplayName("ServiceMVC Тест получения всех букингов пользователя предмета")
    void AllBookingByBooker() {
        List<BookingDto> expectedAllBookings = getBookingList("ALL");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository.findAllByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(bookingPast, bookingCurrent, bookingFuture, bookingRejected));
        List<BookingDto> actualAllBookings = mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "ALL", 0, 5);
        assertEquals(expectedAllBookings, actualAllBookings);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения текущих букингов пользователя предмета")
    void CurrentBookingByBooker() {
        List<BookingDto> expectedCurrentBookings = getBookingList("CURRENT");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findCurrentBookingByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingCurrent, bookingRejected));
        List<BookingDto> actualCurrentBookings = mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "CURRENT", 0, 5);
        assertEquals(expectedCurrentBookings, actualCurrentBookings);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения прошлых букингов пользователя предмета")
    void PastBookingByBooker() {
        List<BookingDto> expectedPastBooking = getBookingList("PAST");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findPastBookingByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingPast));
        List<BookingDto> actualPastBookings = mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "PAST", 0, 5);
        assertEquals(expectedPastBooking, actualPastBookings);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения будущих букингов пользователя предмета")
    void FutureBookingByBooker() {
        List<BookingDto> expectedFutureBooking = getBookingList("FUTURE");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findFutureBookingByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingFuture));
        List<BookingDto> actualFutureBookings = mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "FUTURE", 0, 5);
        assertEquals(expectedFutureBooking, actualFutureBookings);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения ожидающих букингов пользователя предмета")
    void WaitingBookingByBooker() {
        List<BookingDto> expectedWaitingBooking = getBookingList("WAITING");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findByBookerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(bookingFuture));
        List<BookingDto> actualWaitingBookings = mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "WAITING", 0, 5);
        assertEquals(expectedWaitingBooking, actualWaitingBookings);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения отмененных букингов пользователя предмета")
    void RejectedBookingByBooker() {
        List<BookingDto> expectedRejectedBooking = getBookingList("REJECTED");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        when(mockBookingRepository
                .findByBookerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(bookingRejected));
        List<BookingDto> actualRejectedBookings = mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "REJECTED", 0, 5);
        assertEquals(expectedRejectedBooking, actualRejectedBookings);
    }

    @Test
    @DisplayName("ServiceMVC Тест ошибки получения букинга владельца предмета")
    void UnknownStateBookingByOwner() {
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "Unknown state", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", ex.getMessage());
    }

    @Test
    @DisplayName("ServiceMVC Тест получения всех букингов владельца предмета")
    void AllBookingByOwner() {
        List<BookingDto> expectedAllBookings = getBookingList("ALL");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user1);
        when(mockBookingRepository.findAllByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(bookingPast, bookingCurrent, bookingFuture, bookingRejected));
        List<BookingDto> actualAllBookings = mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "ALL", 0, 5);
        assertEquals(expectedAllBookings, actualAllBookings);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения текущих букингов владельца предмета")
    void CurrentBookingByOwner() {
        List<BookingDto> expectedCurrentBookings = getBookingList("CURRENT");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user1);
        when(mockBookingRepository
                .findCurrentBookingByItemOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingCurrent, bookingRejected));
        List<BookingDto> actualCurrentBookings = mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "CURRENT", 0, 5);
        assertEquals(expectedCurrentBookings, actualCurrentBookings);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения прошлых букингов владельца предмета")
    void PastBookingByOwner() {
        List<BookingDto> expectedPastBooking = getBookingList("PAST");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user1);
        when(mockBookingRepository
                .findPastBookingByItemOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingPast));
        List<BookingDto> actualPastBookings = mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "PAST", 0, 5);
        assertEquals(expectedPastBooking, actualPastBookings);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения будущих букингов владельца предмета")
    void FutureBookingByOwner() {
        List<BookingDto> expectedFutureBooking = getBookingList("FUTURE");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user1);
        when(mockBookingRepository
                .findFutureBookingByItemOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingFuture));
        List<BookingDto> actualFutureBookings = mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "FUTURE", 0, 5);
        assertEquals(expectedFutureBooking, actualFutureBookings);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения ожидающих букингов владельца предмета")
    void WaitingBookingByOwner() {
        List<BookingDto> expectedWaitingBooking = getBookingList("WAITING");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user1);
        when(mockBookingRepository
                .findBookingByOwnerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(bookingFuture));
        List<BookingDto> actualWaitingBookings = mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "WAITING", 0, 5);
        assertEquals(expectedWaitingBooking, actualWaitingBookings);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения отменных букингов владельца предмета")
    void RejectedBookingByOwner() {
        List<BookingDto> expectedRejectedBooking = getBookingList("REJECTED");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user1);
        when(mockBookingRepository
                .findBookingByOwnerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(bookingRejected));
        List<BookingDto> actualRejectedBookings = mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "REJECTED", 0, 5);
        assertEquals(expectedRejectedBooking, actualRejectedBookings);
    }

    @Test
    @DisplayName("ServiceMVC Тест добавления букинга предмета")
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
    @DisplayName("ServiceMVC Тест получение букинга предмета")
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
        BookingDto actualBookingDto = mockBookingServiceImpl
                .updateStatusBooking(user.getId(), booking.getId(), true);
        assertEquals(expectedBookingDto, actualBookingDto);
    }

    private List<BookingDto> getBookingList(String state) {
        bookingPast.setStart(LocalDateTime.now().minusDays(10));
        bookingPast.setEnd(LocalDateTime.now().minusDays(5));
        bookingPast.setStatus(BookingStatus.APPROVED);

        bookingCurrent.setStart(LocalDateTime.now().minusDays(4));
        bookingCurrent.setEnd(LocalDateTime.now().plusDays(2));
        bookingCurrent.setStatus(BookingStatus.APPROVED);

        bookingFuture.setStart(LocalDateTime.now().plusDays(5));
        bookingFuture.setEnd(LocalDateTime.now().plusDays(10));

        bookingRejected.setStart(LocalDateTime.now());
        bookingRejected.setEnd(LocalDateTime.now().plusDays(10));
        bookingRejected.setStatus(BookingStatus.REJECTED);
        List<BookingDto> bookingList;
        switch (state) {
            case ("ALL"):
                bookingList = bookingMapper
                        .toBookingDtoList(List.of(bookingPast, bookingCurrent, bookingFuture, bookingRejected));
                break;
            case ("CURRENT"):
                bookingList = bookingMapper
                        .toBookingDtoList(List.of(bookingCurrent, bookingRejected));
                break;
            case ("FUTURE"):
            case ("WAITING"):
                bookingList = bookingMapper.toBookingDtoList(List.of(bookingFuture));
                break;
            case ("PAST"):
                bookingList = bookingMapper.toBookingDtoList(List.of(bookingPast));
                break;
            case ("REJECTED"):
                bookingList = bookingMapper.toBookingDtoList(List.of(bookingRejected));
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList;
    }
}