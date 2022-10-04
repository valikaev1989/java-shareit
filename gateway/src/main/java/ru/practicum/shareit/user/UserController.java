package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.userDto.UserDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addNewUser(@RequestBody UserDto userDto) {
        log.info("GATEWAY start addNewUser: userDto = {}", userDto);
        ResponseEntity<Object> responseEntity = userClient.addNewUser(userDto);
        log.info("GATEWAY end addNewUser: user =  {}", responseEntity);
        return responseEntity;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("GATEWAY start updateUser: userId = {}, userDto = {}", userId, userDto);
        ResponseEntity<Object> responseEntity = userClient.updateUser(userDto, userId);
        log.info("GATEWAY end updateUser: user {}", responseEntity);
        return responseEntity;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GATEWAY start: Get all users");
        ResponseEntity<Object> responseEntity = userClient.getUsers();
        log.info("GATEWAY end getAllUsers: users = {}", responseEntity);
        return responseEntity;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("GATEWAY start: Get userId = {}", userId);
        ResponseEntity<Object> responseEntity = userClient.findById(userId);
        log.info("GATEWAY end: Get user = {}", responseEntity);
        return responseEntity;
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId) {
        log.info("GATEWAY start: Delete user id = {}", userId);
        userClient.deleteUser(userId);
        log.info("GATEWAY end: Delete user id = {}", userId);
    }
}