package ru.practicum.shareit.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class ValidatorServer {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    public User validateAndReturnUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format(
                "пользователь с id '%d' не найден в списке пользователей!", userId)));
    }

    public void validateEmailUser(UserDto userDto) {
        if (userDto.getEmail().isEmpty()) {
            log.warn("отсутствует адрес электронной почты: {}", userDto.getEmail());
            throw new ValidationException("email отсутствует");
        }
        String patternEmail = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+" +
                "@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patternEmail);
        java.util.regex.Matcher matcher = pattern.matcher(userDto.getEmail());
        if (!matcher.matches()) {
            log.warn("Некорректный адрес электронной почты: {}", userDto.getEmail());
            throw new ValidationException("некорректный email");
        }
    }

    public void validateOwnerFromItem(Long userId, Long itemId) {
        if (!userId.equals(validateAndReturnItemByItemId(itemId).getOwner().getId())) {
            log.warn("пользователь с userId '{}' не является владельцем предмета с itemId {}!", userId, itemId);
            throw new UserNotFoundException(String.format("предмет с userId '%d' не является " +
                    "владельцем предмета с itemId '%d'!", userId, itemId));
        }
    }

//    public void validateItemAll(ItemDto itemDto) {
//        validateItemName(itemDto);
//        validateItemDesc(itemDto);
//        if (itemDto.getAvailable() == null) {
//            log.warn("доступность предмета не должна быть пустым!");
//            throw new ValidationException("доступность предмета не должна быть пустым!");
//        }
//    }
//
//    public void validateItemName(ItemDto itemDto) {
//        if (itemDto.getName().isEmpty() || itemDto.getName() == null) {
//            log.warn("имя предмета не должно быть пустым!");
//            throw new ValidationException("имя предмета не должно быть пустым!");
//        }
//    }
//
//    public void validateItemDesc(ItemDto itemDto) {
//        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
//            log.warn("описание предмета не должно быть пустым!");
//            throw new ValidationException("описание предмета не должно быть пустым!");
//        }
//    }

    public Item validateAndReturnItemByItemId(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("предмет с id '%d' не найден в списке предметов!",
                        itemId)));
    }

    public void validateForAddBooking(User user, Item item, BookingDtoOnlyId bookingDtoId) {
        if (user.getId() == item.getOwner().getId()) {
            log.warn("Владелец предмета с id {} не может арендовать у себя предмет с id{}",
                    user.getId(), item.getId());
            throw new ItemNotFoundException(String.format("Владелец предмета с id '%d' не может " +
                    "арендовать у себя предмет с id '%d'", user.getId(), item.getId()));
        }
        if (!item.getAvailable()) {
            log.warn("предмет с id {} занят", item.getId());
            throw new ValidationException(String.format("предмет с id '%d' занят", item.getId()));
        }
//        LocalDateTime startTime = bookingDtoId.getStart();
//        LocalDateTime endTime = bookingDtoId.getEnd();
//        if (endTime.isBefore(LocalDateTime.now())) {
//            log.warn("Время окончания брони раньше текущего времени");
//            throw new ValidationException("Время окончания брони раньше текущего времени");
//        }
//        if (startTime.isAfter(endTime)) {
//            log.warn("Время окончания раньше начала брони");
//            throw new ValidationException("Время окончания раньше начала брони");
//        }
//        if (startTime.isBefore(LocalDateTime.now())) {
//            log.warn("Время начала брони раньше текущего времени");
//            throw new ValidationException("Время начала брони раньше текущего времени");
//        }
    }

    public Booking validateForGetBooking(User user, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ItemNotFoundException(String.format("бронь предмета с bookingId '%d' не найдена!", bookingId)));
        if (user.getId() == booking.getBooker().getId() || user.getId() == booking.getItem().getOwner().getId()) {
            return booking;
        } else {
            log.warn("пользователь с id {} не владелец или пользователь вещи c id {}",
                    user.getId(), booking.getItem().getId());
            throw new ItemNotFoundException(String.format("пользователь с id '%d' не владелец или " +
                    "пользователь вещи c id '%d'", user.getId(), booking.getItem().getId()));
        }
    }

    public Booking validateForUpdateBooking(User user, long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ItemNotFoundException(String.format("бронь предмета с bookingId '%d' не найдена!", bookingId)));
        if (user.getId() != booking.getItem().getOwner().getId()) {
            log.warn("пользователь с id {} не владелец вещи c id {}", user.getId(), booking.getItem().getId());
            throw new ItemNotFoundException(String.format("пользователь с id '%d' не владелец вещи c id '%d'",
                    user.getId(), booking.getItem().getId()));
        }
//        if (approved == null) {
//            log.warn("Approved не может быть пустым");
//            throw new ValidationException("Approved не может быть пустым");
//        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            log.warn("Статус предмета уже изменен(APPROVED)");
            throw new ValidationException("Статус предмета уже изменен(APPROVED)");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.warn("Бронь уже подтверждена(WAITING)");
            throw new ValidationException("Бронь уже подтверждена(WAITING)");
        }
        return booking;
    }

    public void validateBookingForComment(Item item, User booker, CommentDto commentDto) {
//        if (commentDto.getText().isEmpty()) {
//            log.warn("Комментарий не должен быть пустым!");
//            throw new ValidationException("Комментарий не должен быть пустым!");
//        }
        List<Booking> bookingList = bookingRepository.validateForTakeItem(BookingStatus.REJECTED, booker,
                item, LocalDateTime.now());
        if (bookingList.isEmpty()) {
            log.warn("пользователь с id {} не арендовал предмет c id {}", booker.getId(), item.getId());
            throw new ValidationException(String.format("пользователь с id '%d' не арендовал предмет c id '%d'",
                    booker.getId(), item.getId()));
        }
    }


//    public void validateItemRequestDesc(ItemRequestDto itemRequestDto) {
//        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
//            log.warn("описание предмета не должно быть пустым!");
//            throw new ValidationException("описание предмета не должно быть пустым!");
//        }
//    }

    public ItemRequest validateAndReturnItemRequestByRequestId(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() -> new ItemNotFoundException(String.format(
                "запрос предмета с id '%d' не найден в списке запросов!", requestId)));
    }


//    public void validatePage(int from, int size) {
//        if (from < 0) {
//            log.warn("страниц выборки {} не должно быть меньше 0!", from);
//            throw new ValidationException(String.format("страниц выборки '%d' не должно быть меньше 0!", from));
//        }
//        if (size < 1) {
//            log.warn("размер выборки {} не должно быть меньше 1!", size);
//            throw new ValidationException(String.format("размер выборки '%d' не должно быть меньше 1!", size));
//        }
//    }
}