package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.commentDto.CommentDto;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.request.requestDto.RequestDto;
import ru.practicum.shareit.user.userDto.UserDto;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ValidatorGateway {

    public void validateId(Long id) {
        if (id < 0) {
            log.warn("id меньше нуля");
            throw new ValidationException("id меньше нуля");
        }
        if (id == null) {
            log.warn("id =  null");
            throw new ValidationException("id =  null");
        }
    }

    public void validateUserDTO(UserDto userDto) {
        validateNameUser(userDto);
        validateNotNullEmailUser(userDto);
        validateEmailUser(userDto);
    }

    public void validateNameUser(UserDto userDto) {
        if (userDto.getName().isEmpty() || userDto.getName().contains(" ")) {
            log.warn("Логин не должен быть пустым и не должен содержать пробелов");
            throw new ValidationException("некорректный логин");
        }
    }

    public void validateNotNullEmailUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            log.warn("отсутствует адрес электронной почты: {}", userDto.getEmail());
            throw new ValidationException("email отсутствует");
        }
    }

    public void validateEmailUser(UserDto userDto) {
        String patternEmail = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+" +
                "@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patternEmail);
        java.util.regex.Matcher matcher = pattern.matcher(userDto.getEmail());
        if (!matcher.matches()) {
            log.warn("Некорректный адрес электронной почты: {}", userDto.getEmail());
            throw new ValidationException("некорректный email");
        }
    }

    public void validateItemRequestDesc(RequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            log.warn("описание предмета не должно быть пустым!");
            throw new ValidationException("описание предмета не должно быть пустым!");
        }
    }

    public void validatePage(int from, int size) {
        if (from < 0) {
            log.warn("страниц выборки {} не должно быть меньше 0!", from);
            throw new ValidationException(String.format("страниц выборки '%d' не должно быть меньше 0!", from));
        }
        if (size < 1) {
            log.warn("размер выборки {} не должно быть меньше 1!", size);
            throw new ValidationException(String.format("размер выборки '%d' не должно быть меньше 1!", size));
        }
    }

    public void validateCommentText(CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            log.warn("Комментарий не должен быть пустым!");
            throw new ValidationException("Комментарий не должен быть пустым!");
        }
    }

    public void validateItemAll(ItemDto itemDto) {
        validateItemName(itemDto);
        validateItemDesc(itemDto);
        if (itemDto.getAvailable() == null) {
            log.warn("доступность предмета не должна быть пустым!");
            throw new ValidationException("доступность предмета не должна быть пустым!");
        }
    }

    public void validateItemName(ItemDto itemDto) {
        if (itemDto.getName().isEmpty() || itemDto.getName() == null) {
            log.warn("имя предмета не должно быть пустым!");
            throw new ValidationException("имя предмета не должно быть пустым!");
        }
    }

    public void validateItemDesc(ItemDto itemDto) {
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            log.warn("описание предмета не должно быть пустым!");
            throw new ValidationException("описание предмета не должно быть пустым!");
        }
    }

    public void validateItemNameAndDescOnEmpty(ItemDto itemDto) {
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            log.warn("имя предмета и описание предмета не должно быть пустым!");
            throw new ValidationException("имя предмета и описание предмета не должно быть пустым!");
        }
    }

    public BookingState validateStateBooking(String stateParam) {
        return BookingState.from(stateParam).orElseThrow(() ->
                new ValidationException(String.format("Unknown state:\"%s\"", stateParam)));
    }

    public void validateTimeBooking(BookingDto bookingDto) {
        LocalDateTime startTime = bookingDto.getStart();
        LocalDateTime endTime = bookingDto.getEnd();
        if (endTime.isBefore(LocalDateTime.now())) {
            log.warn("Время окончания брони раньше текущего времени");
            throw new ValidationException("Время окончания брони раньше текущего времени");
        }
        if (startTime.isAfter(endTime)) {
            log.warn("Время окончания раньше начала брони");
            throw new ValidationException("Время окончания раньше начала брони");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            log.warn("Время начала брони раньше текущего времени");
            throw new ValidationException("Время начала брони раньше текущего времени");
        }
    }

    public void validateApprovedBooking(Boolean approved) {
        if (approved == null) {
            log.warn("Approved не может быть пустым");
            throw new ValidationException("Approved не может быть пустым");
        }
    }
}