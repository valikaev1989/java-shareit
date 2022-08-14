package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public User toUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public List<UserDto> toUserDto(Collection<User> users) {
        List<UserDto> list = new ArrayList<>();
        for (User user : users) {
            UserDto toUserDto = toUserDto(user);
            list.add(toUserDto);
        }
        return list;
    }
}