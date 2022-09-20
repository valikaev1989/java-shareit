package ru.practicum.shareit.usersTests;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserIntegrationTests extends StorageForTests {

    private final UserController userController;
    private final UserDto user1 = createUserDtoWithoutId();
    private final UserDto user2 = createUserDtoTwoWithoutId();
    private final UserDto user3 = createUserDtoThreeWithoutId();


    @Test
    void contextLoads() {
        assertNotNull(userController);
    }

    @Test
    void testGetAllUsers() {
        UserDto expectedUserDto1 = userController.addUser(user1);
        UserDto expectedUserDto2 = userController.addUser(user2);
        assertEquals(List.of(expectedUserDto1, expectedUserDto2), userController.getAllUsers());
    }

    @Test
    void testCreateAndFindCorrectUser() {
        UserDto expectedUserDto = userController.addUser(user1);
        assertEquals(expectedUserDto, userController.getUserById(expectedUserDto.getId()));
    }

    @Test
    void testCreateUserWithExistingEmail() {
        user2.setEmail(user1.getEmail());
        userController.addUser(user1);
        assertThrows(DataIntegrityViolationException.class, () -> userController.addUser(user2));
    }

    @Test
    void testUpdateUserCorrect() {
        UserDto userDto = userController.addUser(user1);
        userController.updateUser(user2, userDto.getId());
        assertEquals(user2.getName(), userController.getUserById(userDto.getId()).getName());
        assertEquals(user2.getEmail(), userController.getUserById(userDto.getId()).getEmail());
    }

    @Test
    void testUpdateUserExistingEmail() {
        user3.setEmail(user1.getEmail());
        userController.addUser(user1);
        final UserDto userDto2 = userController.addUser(user2);
        assertThrows(DataIntegrityViolationException.class, () -> userController.updateUser(user3, userDto2.getId()));
    }

    @Test
    void testDeleteUser() {
        final UserDto user = userController.addUser(user1);
        final UserDto user1 = userController.addUser(user2);
        userController.deleteUserById(user1.getId());
        assertEquals(List.of(user), userController.getAllUsers());
    }
}