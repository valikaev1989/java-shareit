package ru.practicum.shareit.bookingTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingIntegrationTests extends StorageForTests {
    private final BookingController bookingController;
    private final ItemService itemService;
    private final UserService userService;
    private final CommentService commentService;
    private final UserDto user1 = createUserDtoWithoutId();
    private final UserDto user2 = createUserDtoTwoWithoutId();
    private final ItemDto item1 = createItemDtoNullRequestAndId();
    private final ItemDto item2 = createItemDtoNullRequestAndIdTwo();
    BookingDtoOnlyId bookingDtoOnlyId1 = createBookingForComment();
    BookingDtoOnlyId bookingDtoOnlyId2 = createBookingForComment();

    @Test
    void contextLoads() {
        assertNotNull(bookingController);
        assertNotNull(itemService);
        assertNotNull(userService);
        assertNotNull(commentService);
    }

    @Test
    void getAllBookingsFromUser() {
        UserDto owner = userService.addNewUser(user1);
        UserDto booker = userService.addNewUser(user2);
        itemService.addItem(owner.getId(), item1);
        ItemDto itemDto2 = itemService.addItem(owner.getId(), item2);
        bookingDtoOnlyId2.setItemId(itemDto2.getId());
        BookingDto expectedBookingDto1 = bookingController.addBooking(booker.getId(), bookingDtoOnlyId1);
        BookingDto expectedBookingDto2 = bookingController.addBooking(booker.getId(), bookingDtoOnlyId1);
        List<BookingDto> actualBookingDtoList = bookingController
                .getAllBookingsFromUser(booker.getId(), "ALL", 0, 5);
        List<BookingDto> expectedBookingDtoList = List.of(expectedBookingDto1, expectedBookingDto2);
        assertEquals(expectedBookingDtoList.size(), actualBookingDtoList.size());
        assertEquals(expectedBookingDtoList.get(0).getId(), actualBookingDtoList.get(0).getId());
        assertEquals(expectedBookingDtoList.get(1).getId(), actualBookingDtoList.get(1).getId());
    }

    @Test
    void getBookingByIdOwner() {
        UserDto owner = userService.addNewUser(user1);
        UserDto booker = userService.addNewUser(user2);
        itemService.addItem(owner.getId(), item1);
        ItemDto itemDto2 = itemService.addItem(owner.getId(), item2);
        bookingDtoOnlyId2.setItemId(itemDto2.getId());
        BookingDto expectedBookingDto1 = bookingController.addBooking(booker.getId(), bookingDtoOnlyId1);
        BookingDto expectedBookingDto2 = bookingController.addBooking(booker.getId(), bookingDtoOnlyId1);
        List<BookingDto> actualBookingDtoList = bookingController
                .getBookingByIdOwner(owner.getId(), "ALL", 0, 5);
        List<BookingDto> expectedBookingDtoList = List.of(expectedBookingDto1, expectedBookingDto2);
        assertEquals(expectedBookingDtoList.size(), actualBookingDtoList.size());
        assertEquals(expectedBookingDtoList.get(0).getId(), actualBookingDtoList.get(0).getId());
        assertEquals(expectedBookingDtoList.get(1).getId(), actualBookingDtoList.get(1).getId());
    }

    @Test
    void addBooking() {
        UserDto owner = userService.addNewUser(user1);
        UserDto booker = userService.addNewUser(user2);
        ItemDto itemDto = itemService.addItem(owner.getId(), item1);
        assertThrows(ItemNotFoundException.class, () -> bookingController
                .addBooking(owner.getId(), bookingDtoOnlyId1));
        BookingDto actualBookingDto = bookingController.addBooking(booker.getId(), bookingDtoOnlyId1);
        assertEquals(booker.getId(), actualBookingDto.getBooker().getId());
        assertEquals(itemDto.getId(), actualBookingDto.getItem().getId());
        assertEquals(owner.getId(), actualBookingDto.getItem().getOwnerId());
    }

    @Test
    void updateStatusBooking() {
        UserDto owner = userService.addNewUser(user1);
        UserDto booker = userService.addNewUser(user2);
        itemService.addItem(owner.getId(), item1);
        BookingDto expectedBookingDto = bookingController.addBooking(booker.getId(), bookingDtoOnlyId1);
        BookingDto actualBookingDto = bookingController.updateStatusBooking(
                owner.getId(), true, expectedBookingDto.getId());
        assertEquals(BookingStatus.APPROVED, actualBookingDto.getStatus());
    }

    @Test
    void getBookingById() {
        UserDto owner = userService.addNewUser(user1);
        UserDto booker = userService.addNewUser(user2);
        ItemDto itemDto = itemService.addItem(owner.getId(), item1);
        BookingDto expectedBookingDto = bookingController.addBooking(booker.getId(), bookingDtoOnlyId1);
        BookingDto ownerBookingDto = bookingController.getBookingById(owner.getId(), expectedBookingDto.getId());
        BookingDto bookerBookingDto = bookingController.getBookingById(booker.getId(), expectedBookingDto.getId());
        assertEquals(ownerBookingDto, bookerBookingDto);
        assertEquals(expectedBookingDto.getId(), bookerBookingDto.getId());
        assertEquals(booker.getId(), bookerBookingDto.getBooker().getId());
        assertEquals(itemDto.getId(), bookerBookingDto.getItem().getId());
    }
}