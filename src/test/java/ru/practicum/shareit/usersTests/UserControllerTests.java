package ru.practicum.shareit.usersTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests extends StorageForTests {
    @MockBean
    private UserService userService;

    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Test
    void getAllUser() throws Exception {
        UserDto userDtoOne = createUserDto();
        UserDto userDtoTwo = createUserDtoTwo();
        Mockito.when(userService.getUsers()).thenReturn(List.of(userDtoOne, userDtoTwo));
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(userDtoOne.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(userDtoTwo.getId()), Long.class));
    }

    @Test
    void addUser() throws Exception {
        UserDto userDto = createUserDtoWithoutId();
        UserDto expectedDto = createUserDto();
        Mockito.when(userService.addNewUser(userDto)).thenReturn(expectedDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedDto.getName())))
                .andExpect(jsonPath("$.email", is(expectedDto.getEmail())));
    }

    @Test
    void getUser() throws Exception {
        UserDto expectedDto = createUserDto();
        Mockito.when(userService.findUserDtoById(anyLong())).thenReturn(expectedDto);
        mvc.perform(get("/users/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedDto.getName())))
                .andExpect(jsonPath("$.email", is(expectedDto.getEmail())));
    }

    @Test
    void changeUser() throws Exception {
        UserDto userDto = createUserDtoWithoutId();
        UserDto expectedDto = createUserDto();
        Mockito.when(userService.updateUser(userDto, expectedDto.getId())).thenReturn(expectedDto);
        mvc.perform(patch("/users/" + expectedDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedDto.getName())))
                .andExpect(jsonPath("$.email", is(expectedDto.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/" + anyLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findUserByIncorrectIdTest() throws Exception {
        Mockito.when(userService.findUserDtoById(1L)).thenThrow(UserNotFoundException.class);
        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    void addUserIncorrectName() throws Exception {
        UserDto userDto = createUserDtoWithoutId();
        userDto.setName("");
        Mockito.when(userService.addNewUser(userDto)).thenThrow(ValidationException.class);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    void addUserNull() throws Exception {
        Mockito.when(userService.addNewUser(any(UserDto.class))).thenReturn(null);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(null))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}