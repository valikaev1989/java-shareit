package ru.practicum.shareit.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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



    public void validateOwnerFromItem(Long userId, Long itemId) {
        if (!userId.equals(validateAndReturnItemByItemId(itemId).getOwner().getId())) {
            log.warn("пользователь с userId '{}' не является владельцем предмета с itemId {}!", userId, itemId);
            throw new UserNotFoundException(String.format("пользователь с userId '%d' не является " +
                    "владельцем предмета с itemId '%d'!", userId, itemId));
        }
    }

    public Item validateAndReturnItemByItemId(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("предмет с id '%d' не найден в списке предметов!",
                        itemId)));
    }

    public void validateForAddBooking(User user, Item item) {
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

    public Booking validateForUpdateBooking(User user, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ItemNotFoundException(String.format("бронь предмета с bookingId '%d' не найдена!", bookingId)));
        if (user.getId() != booking.getItem().getOwner().getId()) {
            log.warn("пользователь с id {} не владелец вещи c id {}", user.getId(), booking.getItem().getId());
            throw new ItemNotFoundException(String.format("пользователь с id '%d' не владелец вещи c id '%d'",
                    user.getId(), booking.getItem().getId()));
        }
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

    public void validateBookingForComment(Item item, User booker) {
        log.info("LocalDateTime.now() = {}", LocalDateTime.now());
        List<Booking> bookingList = bookingRepository.validateForTakeItem(BookingStatus.REJECTED, booker,
                item, LocalDateTime.now());
        if (bookingList.isEmpty()) {
            log.warn("пользователь с id {} не арендовал предмет c id {}", booker.getId(), item.getId());
            throw new ValidationException(String.format("пользователь с id '%d' не арендовал предмет c id '%d'",
                    booker.getId(), item.getId()));
        }
    }

    public ItemRequest validateAndReturnItemRequestByRequestId(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() -> new ItemNotFoundException(String.format(
                "запрос предмета с id '%d' не найден в списке запросов!", requestId)));
    }
}