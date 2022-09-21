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
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Validator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ValidatorTests extends StorageForTests {
    private final Validator validator;
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
    @DisplayName("Validator Тест проверки данных пользователя")
    void validateUserDTO() {
        UserDto dto1 = new UserDto();
        dto1.setName("");
        dto1.setEmail("email@email.ru");

        UserDto dto2 = new UserDto();
        dto2.setName("NameDto");

        UserDto dto3 = new UserDto();
        dto3.setName("NameDto");
        dto3.setEmail("email.r@u");

        assertThrows(ValidationException.class, () -> validator.validateUserDTO(dto1));
        assertThrows(ValidationException.class, () -> validator.validateNameUser(dto1));

        assertThrows(ValidationException.class, () -> validator.validateUserDTO(dto2));
        assertThrows(ValidationException.class, () -> validator.validateNotNullEmailUser(dto2));

        assertThrows(ValidationException.class, () -> validator.validateUserDTO(dto3));
        assertThrows(ValidationException.class, () -> validator.validateEmailUser(dto3));
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
    @DisplayName("Validator Тест проверки данных предмета")
    void validateItemAll() {
        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("");
        itemDto1.setDescription("DescriptionItem");
        itemDto1.setAvailable(true);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("NameItem");
        itemDto2.setDescription("");
        itemDto2.setAvailable(true);

        ItemDto itemDto3 = new ItemDto();
        itemDto3.setName("NameItem");
        itemDto3.setDescription("DescriptionItem");

        assertThrows(ValidationException.class, () -> validator.validateItemAll(itemDto1));
        assertThrows(ValidationException.class, () -> validator.validateItemName(itemDto1));

        assertThrows(ValidationException.class, () -> validator.validateItemAll(itemDto2));
        assertThrows(ValidationException.class, () -> validator.validateItemDesc(itemDto2));

        assertThrows(ValidationException.class, () -> validator.validateItemAll(itemDto3));
    }

    @Test
    @DisplayName("Validator Тест наличия в БД предмета")
    void validateAndReturnItemByItemId() {
        assertThrows(ItemNotFoundException.class, () -> validator.validateAndReturnItemByItemId(1L));
        User owner = createUserWithoutId();
        userRepository.save(owner);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        assertEquals(item, validator.validateAndReturnItemByItemId(item.getId()));
    }

    @Test
    @DisplayName("Validator Тест проверок для метода добавления букинга")
    void validateForAddBooking() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        BookingDtoOnlyId bookingDtoOnlyId = createBookingDtoOnlyIdLast();
        bookingDtoOnlyId.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoOnlyId.setEnd(LocalDateTime.now().minusDays(1));

        ItemNotFoundException ex1 = assertThrows(ItemNotFoundException.class, () -> validator
                .validateForAddBooking(owner, item, bookingDtoOnlyId));
        assertEquals("Владелец не может арендовать у себя", ex1.getMessage());
        item.setAvailable(false);
        ValidationException ex2 = assertThrows(ValidationException.class, () -> validator
                .validateForAddBooking(user, item, bookingDtoOnlyId));
        assertEquals("Вешь занята", ex2.getMessage());
        item.setAvailable(true);
        ValidationException ex5 = assertThrows(ValidationException.class, () -> validator
                .validateForAddBooking(user, item, bookingDtoOnlyId));
        assertEquals("Время окончания не корректно", ex5.getMessage());
        bookingDtoOnlyId.setStart(LocalDateTime.now().plusDays(5));
        bookingDtoOnlyId.setEnd(LocalDateTime.now().plusDays(1));
        ValidationException ex3 = assertThrows(ValidationException.class, () -> validator
                .validateForAddBooking(user, item, bookingDtoOnlyId));
        assertEquals("Время окончания раньше начала", ex3.getMessage());
        bookingDtoOnlyId.setStart(LocalDateTime.now().minusDays(5));
        bookingDtoOnlyId.setEnd(LocalDateTime.now().plusDays(1));
        ValidationException ex4 = assertThrows(ValidationException.class, () -> validator
                .validateForAddBooking(user, item, bookingDtoOnlyId));
        assertEquals("Время начала не корректно", ex4.getMessage());
    }

    @Test
    @DisplayName("Validator Тест проверок для получений букинга")
    void validateForGetBooking() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        User otherUser = createUserThreeWithoutId();
        userRepository.save(otherUser);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        assertThrows(ItemNotFoundException.class, () -> validator.validateForGetBooking(owner, 1L));
        Booking booking = createBookingWithoutId(user, item);
        bookingRepository.save(booking);
        assertThrows(ItemNotFoundException.class, () -> validator
                .validateForGetBooking(otherUser, booking.getId()));
        assertEquals(booking, validator.validateForGetBooking(owner, booking.getId()));
        assertEquals(booking, validator.validateForGetBooking(user, booking.getId()));
    }

    @Test
    @DisplayName("Validator Тест проверок для редактирования букинга")
    void validateForUpdateBooking() {
        User owner = createUserWithoutId();
        userRepository.save(owner);
        User user = createUserTwoWithoutId();
        userRepository.save(user);
        Item item = createItemWithoutId(owner);
        itemRepository.save(item);
        Booking booking = createBookingWithoutId(user, item);
        bookingRepository.save(booking);
        Booking booking1 = createBooking2();
        assertEquals(booking, validator.validateForUpdateBooking(owner, booking.getId(), true));
        assertThrows(ItemNotFoundException.class, () -> validator
                .validateForUpdateBooking(user, booking1.getId(), null));
        assertThrows(ItemNotFoundException.class, () -> validator
                .validateForUpdateBooking(user, booking.getId(), null));
        assertThrows(ValidationException.class, () -> validator
                .validateForUpdateBooking(owner, booking.getId(), null));
        booking.setStatus(BookingStatus.APPROVED);
        assertThrows(ValidationException.class, () -> validator
                .validateForUpdateBooking(owner, booking.getId(), true));
        booking.setStatus(BookingStatus.REJECTED);
        assertThrows(ValidationException.class, () -> validator
                .validateForUpdateBooking(owner, booking.getId(), true));
    }

    @Test
    @DisplayName("Validator Тест проверок для создания комментария")
    void validateBookingForComment() {
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
        CommentDto commentDto = createCommentDto();
        commentDto.setText("");
        assertThrows(ValidationException.class, () -> validator
                .validateBookingForComment(item, user, commentDto));
        commentDto.setText("comment");
        assertThrows(ValidationException.class, () -> validator
                .validateBookingForComment(item, otherUser, commentDto));
    }

    @Test
    @DisplayName("Validator Тест проверок описания запроса о предмете")
    void validateItemRequestDesc() {
        ItemRequestDto itemRequestDto = createRequestDto();
        itemRequestDto.setDescription("");
        assertThrows(ValidationException.class, () -> validator
                .validateItemRequestDesc(itemRequestDto));
    }

    @Test
    @DisplayName("Validator Тест проверки наличия в БД запроса о предмете")
    void validateAndReturnItemRequestByRequestId() {
        assertThrows(ItemNotFoundException.class, () -> validator
                .validateAndReturnItemRequestByRequestId(1L));
        User user = createUserWithoutId();
        userRepository.save(user);
        ItemRequest itemRequest = createRequestWithoutId();
        itemRequest.setRequester(user);
        itemRequestRepository.save(itemRequest);
        assertEquals(itemRequest, validator.validateAndReturnItemRequestByRequestId(itemRequest.getId()));
    }

    @Test
    @DisplayName("Validator Тест корректности выдачи страниц результата")
    void validatePage() {
        assertThrows(ValidationException.class, () -> validator.validatePage(-1, 5));
        assertThrows(ValidationException.class, () -> validator.validatePage(1, 0));
    }
}