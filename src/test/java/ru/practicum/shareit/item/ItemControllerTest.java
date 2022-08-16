package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTest {

    private final UserController userController;

    private final ItemController itemController;
    private final ItemRepository itemRepository;

    @Autowired
    ItemControllerTest(UserController userController, ItemController itemController, ItemRepository itemRepository) {
        this.userController = userController;
        this.itemController = itemController;
        this.itemRepository = itemRepository;
    }

    private final UserDto user1 = UserDto.builder().name("test1").email("test1@test1.com").build();

    private final ItemDto item1 = ItemDto.builder().name("item1").description("item1").available(true).build();
    private final ItemDto item2 = ItemDto.builder().name("item2").description("item2").available(true).build();

    @Test
    void contextLoads() {
        assertNotNull(itemController);
    }

    @BeforeEach
    void cleanTemp() {
        for (UserDto userDto : userController.getAllUsers()) {
            userController.deleteUserById(userDto.getId());
        }
        itemRepository.getAllItem().clear();
    }

    @Test
    void testCreateAndFindCorrectItem() {
        final UserDto userDto = userController.addUser(user1);
        final ItemDto itemDto = itemController.addNewItem(userDto.getId(), item1);
        assertEquals(1, itemController.findById(itemDto.getId()).getId());
    }

    @Test
    void testCreateBlankName() {
        final UserDto userDto = userController.addUser(user1);
        assertThrows(ValidationException.class, () -> itemController.addNewItem(userDto.getId(),
                item1.toBuilder().name("").build()));
    }

    @Test
    void testCreateBlankDescription() {
        final UserDto userDto = userController.addUser(user1);
        assertThrows(ValidationException.class, () -> itemController.addNewItem(userDto.getId(),
                item1.toBuilder().description("").build()));
    }

    @Test
    void testFindItemsByOwnerId() {
        final UserDto userDto = userController.addUser(user1);
        itemController.addNewItem(userDto.getId(), item1);
        itemController.addNewItem(userDto.getId(), item2);
        assertEquals(2, itemController.findAllByUserId(userDto.getId()).size());
    }

    @Test
    void testDeleteItemByOwner() {
        final UserDto userDto = userController.addUser(user1);
        itemController.addNewItem(userDto.getId(), item1);
        final ItemDto itemDto2 = itemController.addNewItem(userDto.getId(), item2);
        itemController.deleteItem(userDto.getId(), itemDto2.getId());
        assertEquals(1, itemController.findAllByUserId(userDto.getId()).size());
    }

    @Test
    void testSearchItemByDescription() {
        final UserDto userDto = userController.addUser(user1);
        final ItemDto itemDto = itemController.addNewItem(userDto.getId(), item1.toBuilder().name("roBOcoP").build());
        assertEquals(List.of(itemDto), itemController.findByDescription("oboc"));
    }
}