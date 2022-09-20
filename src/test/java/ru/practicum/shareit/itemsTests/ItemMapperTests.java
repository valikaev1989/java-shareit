package ru.practicum.shareit.itemsTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemMapperTests extends StorageForTests {
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Test
    void toItem() {
        User user = createUser();
        ItemDto itemDto = createItemDtoNullRequest();
        Item expectedItem = createItemNullRequest();
        Item actualItem = itemMapper.toItem(itemDto, user);
        actualItem.setId(itemDto.getId());
        assertEquals(expectedItem.toString(), actualItem.toString());
    }

    @Test
    void toItemDto() {
        Item item = createItemNullRequest();
        ItemDto expectedItemDto = createItemDtoNullRequest();
        ItemDto actualItemDto = itemMapper.toItemDto(item);
        assertEquals(expectedItemDto, actualItemDto);
        Item itemWithRequest = createItemWithRequest();
        ItemDto expectedItemDtoWithRequest = createItemDtoWithRequestId();
        ItemDto actualItemDtoWithRequest = itemMapper.toItemDto(itemWithRequest);
        assertEquals(expectedItemDtoWithRequest, actualItemDtoWithRequest);
    }

    @Test
    void toItemOwnerDto() {
        Item item = createItemNullRequest();
        Comment comment = createComment();
        List<CommentDto> commentDtoList = commentMapper.toCommentDtoList(List.of(comment));
        BookingDtoOnlyId last = createBookingDtoOnlyIdLast();
        BookingDtoOnlyId next = createBookingDtoOnlyIdNext();
        ItemOwnerDto expectedItemOwnerDto = itemMapper.toItemOwnerDto(item, commentDtoList, last, next);
        ItemOwnerDto expectedItemUserDto = itemMapper.toItemOwnerDto(item, commentDtoList, null, null);
        ItemOwnerDto actualWithOwner = createItemOwnerDto();
        ItemOwnerDto actualWithUser = createItemUserDto();
        assertEquals(expectedItemOwnerDto, actualWithOwner);
        assertEquals(expectedItemUserDto, actualWithUser);
    }

    @Test
    void ToItemDtoList() {
        List<ItemDto> expectedItemDtoList = List.of(createItemDtoNullRequest());
        List<ItemDto> actualItemDtoList = itemMapper.toItemDtoList(List.of(createItemNullRequest()));
        assertEquals(expectedItemDtoList, actualItemDtoList);
        assertEquals(createItemDtoNullRequest(),actualItemDtoList.get(0));
    }
}