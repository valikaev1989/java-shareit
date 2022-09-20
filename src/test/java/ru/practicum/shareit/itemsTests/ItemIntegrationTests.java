package ru.practicum.shareit.itemsTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemIntegrationTests extends StorageForTests {

    private final UserService userService;
    private final ItemController itemController;
    private final BookingService bookingService;
    private final UserDto user1 = createUserDtoWithoutId();
    private final UserDto user2 = createUserDtoTwoWithoutId();
    private final ItemDto item1 = createItemDtoNullRequestAndId();
    private final ItemDto item2 = createItemDtoNullRequestAndIdTwo();
    private final CommentDto commentDto = createCommentDto();

    @Test
    void contextLoads() {
        assertNotNull(itemController);
        assertNotNull(userService);
        assertNotNull(bookingService);
    }

    @Test
    void testCreateAndFindCorrectItem() {
        UserDto userDto = userService.addNewUser(user1);
        ItemDto itemDto = itemController.addNewItem(userDto.getId(), item1);
        assertEquals(itemDto.getId(), itemController.findItemById(itemDto.getId(), userDto.getId()).getId());
    }

    @Test
    void testFindItemsByOwnerId() {
        UserDto userDto = userService.addNewUser(user1);
        itemController.addNewItem(userDto.getId(), item1);
        itemController.addNewItem(userDto.getId(), item2);
        System.out.println(itemController.findAllByUserId(userDto.getId(), 0, 5));
        assertEquals(2, itemController.findAllByUserId(userDto.getId(), 0, 5).size());
    }

    @Test
    void testDeleteItemByOwner() {
        UserDto userDto = userService.addNewUser(user1);
        ItemDto expectedItem = itemController.addNewItem(userDto.getId(), item1);
        ItemDto itemDto2 = itemController.addNewItem(userDto.getId(), item2);
        itemController.deleteItem(userDto.getId(), itemDto2.getId());
        assertEquals(expectedItem.getId(), itemController
                .findAllByUserId(userDto.getId(), 0, 5).get(0).getId());
        assertEquals(1, itemController.findAllByUserId(userDto.getId(), 0, 5).size());
    }

    @Test
    void testSearchItemByDescription() {
        UserDto userDto = userService.addNewUser(user1);
        item1.setName("roBOcoP");
        ItemDto itemDto = itemController.addNewItem(userDto.getId(), item1);
        assertEquals(List.of(itemDto), itemController.findItemByText("oboc", 0, 5));
    }

    @Test
    void testAddComment() throws InterruptedException {
        UserDto owner = userService.addNewUser(user1);
        UserDto booker = userService.addNewUser(user2);
        ItemDto item = itemController.addNewItem(owner.getId(), item1);
        BookingDtoOnlyId bookingDtoOnlyId = createBookingForComment();
        bookingService.addBooking(booker.getId(), bookingDtoOnlyId);
        TimeUnit.SECONDS.sleep(2);
        commentDto.setCreated(LocalDateTime.now());
        CommentDto expectedCommentDto = itemController.addComment(booker.getId(), item.getId(), commentDto);
        ItemOwnerDto actualItemDto = itemController.findItemById(owner.getId(), item.getId());
        assertEquals(item.getId(), actualItemDto.getId());
        assertEquals(expectedCommentDto.getId(), actualItemDto.getComments().get(0).getId());
    }
}