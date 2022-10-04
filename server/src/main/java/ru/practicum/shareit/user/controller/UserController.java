package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        log.info("Add new user {}", userDto);
        return userService.addNewUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Update user id = {}, new user = {}", userId, userDto);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtoList = userService.getUsers();
        log.info("Total users = {}", userDtoList.size());
        return userDtoList;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Get user id = {}", userId);
        return userService.findUserDtoById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        log.info("Delete user id = {}", userId);
        userService.deleteUser(userId);
    }
}