package ru.practicum.shareit.itemRequestsTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestIntegrationTests extends StorageForTests {
    private final ItemRequestController controller;
    private final UserService userService;
    private final ItemService itemService;
    private final UserDto user1 = createUserDtoWithoutId();
    private final UserDto user2 = createUserDtoTwoWithoutId();
    private final UserDto user3 = createUserDtoThreeWithoutId();
    private final ItemDto item1 = createItemDtoWithRequestIdNullOwner();
    private final ItemRequestDto request = createRequestDtoNullId();

    @Test
    void contextLoads() {
        assertNotNull(controller);
        assertNotNull(userService);
        assertNotNull(itemService);
    }

    @Test
    void getOwnRequests() {
        UserDto userDto = userService.addNewUser(user1);
        UserDto userDto1 = userService.addNewUser(user2);
        ItemRequestDto expectedRequestDto = controller.addNewItemRequest(userDto.getId(), request);
        ItemDto itemDto = itemService.addItem(userDto1.getId(), item1);
        List<ItemRequestWithItemDto> actualRequestDto = controller.getOwnRequests(userDto.getId());
        assertEquals(expectedRequestDto.getId(), actualRequestDto.get(0).getId());
        assertEquals(itemDto.getId(), actualRequestDto.get(0).getItems().get(0).getId());
    }

    @Test
    void getAllRequests() {
        UserDto userDto1 = userService.addNewUser(user1);
        UserDto userDto2 = userService.addNewUser(user2);
        UserDto userDto3 = userService.addNewUser(user3);
        ItemRequestDto expectedRequestDto1 = controller.addNewItemRequest(userDto1.getId(), request);
        ItemRequestDto expectedRequestDto2 = controller.addNewItemRequest(userDto2.getId(), request);
        List<ItemRequestWithItemDto> actualRequestList = controller
                .getAllRequests(userDto3.getId(), 0, 5);
        assertEquals(expectedRequestDto1.getId(), actualRequestList.get(0).getId());
        assertEquals(expectedRequestDto2.getId(), actualRequestList.get(1).getId());
    }

    @Test
    void addNewItemRequest() {
        UserDto userDto = userService.addNewUser(user1);
        ItemRequestDto itemRequestDto = controller.addNewItemRequest(userDto.getId(), request);
        assertEquals(userDto.getId(), itemRequestDto.getRequesterId());
    }

    @Test
    void getRequest() {
        UserDto userDto = userService.addNewUser(user1);
        UserDto userDto1 = userService.addNewUser(user2);
        ItemRequestDto expectedRequestDto = controller.addNewItemRequest(userDto.getId(), request);
        ItemRequestWithItemDto actualRequestDto = controller
                .getRequest(userDto1.getId(), expectedRequestDto.getId());
        assertEquals(expectedRequestDto.getId(), actualRequestDto.getId());
    }
}