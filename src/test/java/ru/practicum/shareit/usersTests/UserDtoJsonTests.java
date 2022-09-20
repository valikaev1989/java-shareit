package ru.practicum.shareit.usersTests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

@JsonTest
public class UserDtoJsonTests extends StorageForTests {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDto1() throws IOException {
        UserDto userDto = createUserDto();
        JsonContent<UserDto> result = json.write(userDto);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo((int)userDto.getId());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(userDto.getName());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo(userDto.getEmail());
    }
}