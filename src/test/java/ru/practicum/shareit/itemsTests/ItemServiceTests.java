package ru.practicum.shareit.itemsTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validator;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemServiceTests extends StorageForTests {

    private ItemServiceImpl mockItemService;
    @Autowired
    private ItemMapper itemMapper;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    @Autowired
    private BookingMapper bookingMapper;
    @Mock
    private CommentService mockCommentService;
    @Mock
    private Validator mockValidator;

    @BeforeEach
    void setUp() {
        mockItemService = new ItemServiceImpl(
                itemMapper, mockItemRepository, mockBookingRepository,
                bookingMapper, mockCommentService, mockValidator);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения все предметов пользователя")
    void getAllUserItems() {
        User user = createUser();
        Item item1 = createItemWithRequest();
        Booking lastBooking = createLastBooking();
        Booking nextBooking = createNextBooking();
        CommentDto commentDto = createCommentDto2();
        ItemOwnerDto expectedItemOwnerDto = createItemOwnerDto();
        expectedItemOwnerDto.setLastBooking(bookingMapper.toBookingDtoOnlyId(lastBooking));
        expectedItemOwnerDto.setNextBooking(bookingMapper.toBookingDtoOnlyId(nextBooking));
        List<ItemOwnerDto> expectedList = List.of(expectedItemOwnerDto);
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockItemRepository.findByOwnerIdOrderById(anyLong(), any(Pageable.class))).thenReturn(List.of(item1));
        when(mockValidator.validateAndReturnItemByItemId(anyLong())).thenReturn(item1);
        when(mockBookingRepository.findFirstByItemOrderByStartAsc(any(Item.class))).thenReturn(lastBooking);
        when(mockBookingRepository.findFirstByItemOrderByEndDesc(any(Item.class))).thenReturn(nextBooking);
        when(mockCommentService.getCommentsByItemId(item1.getId())).thenReturn(List.of(commentDto));
        List<ItemOwnerDto> actualList = mockItemService.getAllUserItems(user.getId(), 0, 5);
        assertEquals(expectedList, actualList);
    }

    @Test
    @DisplayName("ServiceMVC Тест списка предметов по фрагменту текста в названии предмета")
    void findItemsByText() {
        Item item1 = createItemNullRequest();
        Item item2 = createItemNullRequest2();
        ItemDto itemDto1 = createItemDtoNullRequest();
        ItemDto itemDto2 = createItemDtoNullRequest2();
        List<ItemDto> expectedList = List.of(itemDto1, itemDto2);
        when(mockItemRepository.searchItemByNameAndDesc(any(String.class),
                any(Pageable.class))).thenReturn(List.of(item1, item2));
        List<ItemDto> actualList = mockItemService.findItemsByText("item", 0, 5);
        assertEquals(expectedList, actualList);

        List<ItemDto> actualEmptyList = mockItemService.findItemsByText("", 0, 5);
        assertEquals(List.of(), actualEmptyList);
    }

    @Test
    @DisplayName("ServiceMVC Тест списка предметов по пустому тексту в названии предмета")
    void findItemsByText2() {
        List<ItemDto> actualEmptyList = mockItemService.findItemsByText("", 0, 5);
        assertEquals(List.of(), actualEmptyList);
    }

    @Test
    @DisplayName("ServiceMVC Тест добавления предмета")
    void addItem() {
        User user = createUser();
        Item item = createItemWithRequest();
        ItemDto itemDto = createItemDtoWithRequestIdNullOwner();
        ItemDto expectedItemDto = createItemDtoWithRequestId();
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockItemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto actualItemDto = mockItemService.addItem(user.getId(), itemDto);
        assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения предметов владельца со списком комментариев и бронирований")
    void findItemOwnerDtoById1() {
        User user = createUser();
        Item item = createItemWithRequest();
        Booking lastBooking = createLastBooking();
        Booking nextBooking = createNextBooking();
        CommentDto commentDto = createCommentDto2();
        ItemOwnerDto expectedItemOwnerDto = createItemOwnerDto();
        expectedItemOwnerDto.setLastBooking(bookingMapper.toBookingDtoOnlyId(lastBooking));
        expectedItemOwnerDto.setNextBooking(bookingMapper.toBookingDtoOnlyId(nextBooking));

        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockValidator.validateAndReturnItemByItemId(anyLong())).thenReturn(item);
        when(mockBookingRepository.findFirstByItemOrderByStartAsc(any(Item.class))).thenReturn(lastBooking);
        when(mockBookingRepository.findFirstByItemOrderByEndDesc(any(Item.class))).thenReturn(nextBooking);
        when(mockCommentService.getCommentsByItemId(item.getId())).thenReturn(List.of(commentDto));
        ItemOwnerDto actualItemOwnerDto = mockItemService.findItemOwnerDtoById(user.getId(), item.getId());
        assertEquals(expectedItemOwnerDto, actualItemOwnerDto);
    }

    @Test
    @DisplayName("ServiceMVC Тест получения предметов пользователем со списком комментариев и бронирований")
    void findItemOwnerDtoById2() {
        User otherUser = createUserTwo();
        Item item = createItemWithRequest();
        CommentDto commentDto = createCommentDto2();
        ItemOwnerDto expectedItemOwnerDto = createItemOwnerDto();
        expectedItemOwnerDto.setLastBooking(null);
        expectedItemOwnerDto.setNextBooking(null);

        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(otherUser);
        when(mockValidator.validateAndReturnItemByItemId(anyLong())).thenReturn(item);
        when(mockBookingRepository.findFirstByItemOrderByStartAsc(any(Item.class))).thenReturn(null);
        when(mockBookingRepository.findFirstByItemOrderByEndDesc(any(Item.class))).thenReturn(null);
        when(mockCommentService.getCommentsByItemId(item.getId())).thenReturn(List.of(commentDto));

        ItemOwnerDto itemOtherUserDto = mockItemService.findItemOwnerDtoById(otherUser.getId(), item.getId());
        assertEquals(expectedItemOwnerDto, itemOtherUserDto);
    }

    @Test
    @DisplayName("ServiceMVC Тест редактирования названия предмета")
    void updateItem() {
        User user = createUser();
        Item item = createItemWithRequest();
        ItemDto itemDto = createItemDtoWithRequestId();
        itemDto.setName("test");
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockValidator.validateAndReturnItemByItemId(anyLong())).thenReturn(item);
        when(mockItemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto actualItemDto1 = mockItemService.updateItem(user.getId(), item.getId(), itemDto);
        assertEquals(itemDto.getName(), actualItemDto1.getName());
    }

    @Test
    @DisplayName("ServiceMVC Тест редактирования описания предмета")
    void updateItem2() {
        User user = createUser();
        Item item = createItemWithRequest();
        ItemDto itemDto = createItemDtoWithRequestId();
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockValidator.validateAndReturnItemByItemId(anyLong())).thenReturn(item);
        when(mockItemRepository.save(any(Item.class))).thenReturn(item);

        itemDto.setDescription("testDesc");
        ItemDto actualItemDto2 = mockItemService.updateItem(user.getId(), item.getId(), itemDto);
        assertEquals(itemDto.getName(), actualItemDto2.getName());
    }

    @Test
    @DisplayName("ServiceMVC Тест редактирования подтверждения брони предмета")
    void updateItem3() {
        User user = createUser();
        Item item = createItemWithRequest();
        ItemDto itemDto = createItemDtoWithRequestId();
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockValidator.validateAndReturnItemByItemId(anyLong())).thenReturn(item);
        when(mockItemRepository.save(any(Item.class))).thenReturn(item);

        itemDto.setAvailable(false);
        ItemDto actualItemDto3 = mockItemService.updateItem(user.getId(), item.getId(), itemDto);
        assertEquals(itemDto.getName(), actualItemDto3.getName());
    }

    @Test
    @DisplayName("ServiceMVC Тест удаления предмета")
    void deleteItemById() {
        User user = createUser();
        Item item = createItemWithRequest();
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockValidator.validateAndReturnItemByItemId(anyLong())).thenReturn(item);
        mockItemService.deleteItemById(user.getId(), item.getId());
        Mockito.verify(mockItemRepository, Mockito.times(1)).deleteById(item.getId());
    }
}