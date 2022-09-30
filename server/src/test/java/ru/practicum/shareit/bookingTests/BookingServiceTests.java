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
import ru.practicum.shareit.util.ValidatorServer;

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
    private ValidatorServer mockValidator;
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
    void unknownStateBookingByBooker() {
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user2);
        ValidationException ex = assertThrows(ValidationException.class, () -> mockBookingServiceImpl
                .getBookingsByBookerId(user2.getId(), "Unknown state", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", ex.getMessage());
    }

    @Test
    @DisplayName("ServiceMVC Тест получения всех букингов пользователя предмета")
    void allBookingByBooker() {
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
    void currentBookingByBooker() {
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
    void pastBookingByBooker() {
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
    void futureBookingByBooker() {
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
    void waitingBookingByBooker() {
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
    void rejectedBookingByBooker() {
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
    void unknownStateBookingByOwner() {
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> mockBookingServiceImpl
                .getBookingsByOwnerId(user1.getId(), "Unknown state", 0, 5));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", ex.getMessage());
    }

    @Test
    @DisplayName("ServiceMVC Тест получения всех букингов владельца предмета")
    void allBookingByOwner() {
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
    void currentBookingByOwner() {
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
    void pastBookingByOwner() {
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
    void futureBookingByOwner() {
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
    void waitingBookingByOwner() {
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
    void rejectedBookingByOwner() {
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
    @DisplayName("ServiceMVC Тест смены статуса букинга предмета")
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

    /**
     * Шаблон получения списка букинга для пользователя и владельца предмета
     *
     * @param state фильтр поиска
     */
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