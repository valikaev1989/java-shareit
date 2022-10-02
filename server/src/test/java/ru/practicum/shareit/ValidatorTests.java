package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.ValidatorServer;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ValidatorTests extends StorageForTests {
    private final ValidatorServer validator;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Test
    @DisplayName("Validator Тест проверки наличие в БД пользователя")
    void validateAndReturnUserByUserId() {
        assertThrows(UserNotFoundException.class, () -> validator.validateAndReturnUserByUserId(1L));
        User user = createUserWithoutId();
        userRepository.save(user);
        assertEquals(user, validator.validateAndReturnUserByUserId(user.getId()));
    }

    @Test
    @DisplayName("Validator Тест владения пользователем предмета")
    void validateOwnerFromItem() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        assertThrows(UserNotFoundException.class, () -> validator.validateOwnerFromItem(user.getId(), item.getId()));
    }

    @Test
    @DisplayName("Validator Тест наличия в БД предмета")
    void validateAndReturnItemByItemId() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        assertEquals(item, validator.validateAndReturnItemByItemId(item.getId()));
    }

    @Test
    @DisplayName("Validator Тест наличия в БД предмета")
    void validateAndReturnItemByItemId2() {
        assertThrows(ItemNotFoundException.class, () -> validator.validateAndReturnItemByItemId(999L));
    }

    @Test
    @DisplayName("Validator Тест проверок для метода добавления букинга владельцем предмета")
    void validateForAddBooking1() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);

        ItemNotFoundException ex1 = assertThrows(ItemNotFoundException.class, () -> validator
                .validateForAddBooking(owner, item));
        assertEquals(String.format("Владелец предмета с id '%d' не может " +
                "арендовать у себя предмет с id '%d'", owner.getId(), item.getId()), ex1.getMessage());
    }

    @Test
    @DisplayName("Validator Тест проверок доступности предмета для метода добавления букинга")
    void validateForAddBooking2() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        BookingDtoOnlyId bookingDtoOnlyId = createBookingDtoOnlyIdLast();
        bookingDtoOnlyId.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoOnlyId.setEnd(LocalDateTime.now().minusDays(1));

        item.setAvailable(false);
        ValidationException ex2 = assertThrows(ValidationException.class, () -> validator
                .validateForAddBooking(user, item));
        assertEquals(String.format("предмет с id '%d' занят", item.getId()), ex2.getMessage());
    }

    @Test
    @DisplayName("Validator Тест проверки отсутсвия в БД брони для получений букинга")
    void validateForGetBooking() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class, () -> validator
                .validateForGetBooking(owner, 99));
        assertEquals(String.format("бронь предмета с bookingId '%d' не найдена!", 99), ex.getMessage());
    }

    @Test
    @DisplayName("Validator Тест проверок для получений букинга другими пользователями")
    void validateForGetBooking2() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        User otherUser = createUserThreeWithoutId();
        userRepository.save(otherUser);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        Booking booking = createBookingWithoutId(user, item);
        bookingRepository.save(booking);
        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class, () -> validator
                .validateForGetBooking(otherUser, booking.getId()));
        assertEquals(String.format("пользователь с id '%d' не владелец или " + "пользователь вещи c id '%d'",
                otherUser.getId(), booking.getItem().getId()), ex.getMessage());
    }

    @Test
    @DisplayName("Validator Тест проверок для получений букинга владельцем предмета")
    void validateForGetBooking3() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        User otherUser = createUserThreeWithoutId();
        userRepository.save(otherUser);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        Booking booking = createBookingWithoutId(user, item);
        bookingRepository.save(booking);
        assertEquals(booking, validator.validateForGetBooking(owner, booking.getId()));
    }

    @Test
    @DisplayName("Validator Тест проверок для получений букинга пользователем предмета")
    void validateForGetBooking4() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        User otherUser = createUserThreeWithoutId();
        userRepository.save(otherUser);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        Booking booking = createBookingWithoutId(user, item);
        bookingRepository.save(booking);
        assertEquals(booking, validator.validateForGetBooking(user, booking.getId()));
    }

    @Test
    @DisplayName("Validator Тест проверок для редактирования букинга владельцем предмета")
    void validateForUpdateBooking() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        Booking booking = createBookingWithoutId(user, item);
        bookingRepository.save(booking);
        assertEquals(booking, validator.validateForUpdateBooking(owner, booking.getId()));
    }

    @Test
    @DisplayName("Validator Тест проверок наличие брони в БД для редактирования букинга")
    void validateForUpdateBooking2() {
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        Booking booking = createBooking();
        booking.setId(99);
        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class, () -> validator
                .validateForUpdateBooking(user, booking.getId()));
        assertEquals(String.format("бронь предмета с bookingId '%d' не найдена!", booking.getId()), ex.getMessage());
    }

    @Test
    @DisplayName("Validator Тест проверок на право владения предметом для редактирования букинга")
    void validateForUpdateBooking3() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        Booking booking = createBookingWithoutId(user, item);
        bookingRepository.save(booking);
        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class, () -> validator
                .validateForUpdateBooking(user, booking.getId()));
        assertEquals(String.format("пользователь с id '%d' не владелец вещи c id '%d'",
                user.getId(), booking.getItem().getId()), ex.getMessage());
    }

    @Test
    @DisplayName("Validator Тест проверок на подтвержденный статус  для редактирования букинга")
    void validateForUpdateBooking5() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        Booking booking = createBookingWithoutId(user, item);
        bookingRepository.save(booking);

        booking.setStatus(BookingStatus.APPROVED);
        ValidationException ex = assertThrows(ValidationException.class, () -> validator
                .validateForUpdateBooking(owner, booking.getId()));
        assertEquals("Статус предмета уже изменен(APPROVED)", ex.getMessage());
    }

    @Test
    @DisplayName("Validator Тест проверок ожидающий статус для редактирования букинга")
    void validateForUpdateBooking6() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        Booking booking = createBookingWithoutId(user, item);
        bookingRepository.save(booking);

        booking.setStatus(BookingStatus.REJECTED);
        ValidationException ex = assertThrows(ValidationException.class, () -> validator
                .validateForUpdateBooking(owner, booking.getId()));
        assertEquals("Бронь уже подтверждена(WAITING)", ex.getMessage());
    }

    @Test
    @DisplayName("Validator Тест проверок брони предмета пользователем для создания комментария")
    void validateBookingForComment2() {
        User owner = createUserTwoWithoutId();
        userRepository.save(owner);
        User user = createUserWithoutId();
        userRepository.save(user);
        User otherUser = createUserThreeWithoutId();
        userRepository.save(otherUser);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        Booking booking = createBookingWithoutId(user, item);
        bookingRepository.save(booking);
        ValidationException ex = assertThrows(ValidationException.class, () -> validator
                .validateBookingForComment(item, otherUser));
        assertEquals(String.format("пользователь с id '%d' не арендовал предмет c id '%d'",
                otherUser.getId(), item.getId()), ex.getMessage());
    }

    @Test
    @DisplayName("Validator Тест проверки наличия в БД запроса о предмете")
    void validateAndReturnItemRequestByRequestId1() {
        assertThrows(ItemNotFoundException.class, () -> validator.validateAndReturnItemRequestByRequestId(99L));
    }

    @Test
    @DisplayName("Validator Тест проверки наличия в БД запроса о предмете")
    void validateAndReturnItemRequestByRequestId2() {
        User user = createUserWithoutId();
        userRepository.save(user);
        ItemRequest itemRequest = createRequestWithoutId();
        itemRequest.setRequester(user);
        itemRequestRepository.save(itemRequest);
        assertEquals(itemRequest, validator.validateAndReturnItemRequestByRequestId(itemRequest.getId()));
    }
}