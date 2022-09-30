package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.util.ValidatorGateway;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;
    private final ValidatorGateway validator;

    @PostMapping
    public ResponseEntity<Object> addNewUser(@RequestBody UserDto userDto) {
        log.info("GATEWAY start: Add new user {}", userDto);
        validator.validateUserDTO(userDto);
        ResponseEntity<Object> responseEntity = userClient.addNewUser(userDto);
        log.info("GATEWAY end: Add new user {}", responseEntity);
        return responseEntity;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("GATEWAY start: Update user id = {}, new user = {}", userId, userDto);
        validator.validateId(userId);
        ResponseEntity<Object> responseEntity = userClient.updateUser(userDto, userId);
        log.info("GATEWAY end: Update user {}", responseEntity);
        return responseEntity;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GATEWAY: Get all users");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable long userId) {
        log.info("GATEWAY start: Get user id = {}", userId);
        validator.validateId(userId);
        ResponseEntity<Object> responseEntity = userClient.findById(userId);
        log.info("GATEWAY end: Get user id = {}", userId);
        return responseEntity;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("GATEWAY: Delete user id = {}", userId);
        validator.validateId(userId);
        userClient.deleteUser(userId);
    }
}