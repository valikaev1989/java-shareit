package ru.practicum.shareit.usersTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserMapperTests extends StorageForTests {
    private final UserMapper userMapper;

    @Test
    void toUser() {
        User userEntity = userMapper.toUser(createUserDto());
        userEntity.setId(1);
        assertEquals(userEntity.toString(), createUser().toString());
    }

    @Test
    void toUserDto() {
        UserDto expectedUserDto = userMapper.toUserDto(createUser());
        assertEquals(expectedUserDto.toString(), createUserDto().toString());
    }

    @Test
    void toUserDtoList() {
        List<UserDto> userDtoList = userMapper.toUserDtoList(List.of(createUser()));
        assertEquals(userDtoList.size(), List.of(createUser()).size());
        assertEquals(createUserDto(), userDtoList.get(0));
    }
}