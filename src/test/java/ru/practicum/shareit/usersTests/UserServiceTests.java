package ru.practicum.shareit.usersTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.Validator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTests extends StorageForTests {
    @Mock
    private UserRepository mockUserRepository;
    @Autowired
    private UserMapper userMapper;
    @Mock
    private Validator validator;
    UserServiceImpl mockUserService;

    @BeforeEach
    void setUp() {
        mockUserService = new UserServiceImpl(mockUserRepository, userMapper, validator);
    }

    @Test
    void getUsers() {
        User user1 = createUser();
        User user2 = createUserTwo();
        UserDto userDto1 = createUserDto();
        UserDto userDto2 = createUserDtoTwo();
        List<UserDto> expectedList = List.of(userDto1, userDto2);
        when(mockUserRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> actualList = mockUserService.getUsers();
        assertEquals(expectedList, actualList);
        assertEquals(userDto1, actualList.get(0));
        assertEquals(userDto2, actualList.get(1));
    }

    @Test
    void addNewUser() {
        UserDto expectedUserDto = createUserDto();
        User user1 = createUser();
        UserDto userDtoWithoutId = createUserDtoWithoutId();
        when(mockUserRepository.save(any(User.class))).thenReturn(user1);
        UserDto actualUserDto = mockUserService.addNewUser(userDtoWithoutId);
        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void updateUser() {
        User user = createUser();
        UserDto expectedUserDto = createUserDto();
        expectedUserDto.setName("testName");
        when(validator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        user.setName("testName");
        when(mockUserRepository.save(any(User.class))).thenReturn(user);
        UserDto actualUserDto = mockUserService.updateUser(expectedUserDto, user.getId());
        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void findUserDtoById() {
        User user = createUser();
        UserDto expectedUserDto = createUserDto();
        when(validator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        UserDto actualUserDto = mockUserService.findUserDtoById(user.getId());
        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void deleteUser() {
        User user = createUser();
        when(validator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        mockUserService.deleteUser(user.getId());
        Mockito.verify(mockUserRepository, Mockito.times(1)).deleteById(user.getId());
    }
}