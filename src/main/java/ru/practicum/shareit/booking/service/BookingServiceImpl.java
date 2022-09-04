package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final Validator validator;


    /**
     * Получение списка всех бронирований пользователя.
     *
     * @param userId id пользователя
     * @param state  состояние бронирования
     */
    @Override
    public List<BookingDto> getAllBookingsByBookerId(long userId, String state) {
       User user =  validator.validateAndReturnUserByUserId(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ("ALL"):
                bookings.addAll(bookingRepository.findByBookerIdOrderByStartDesc(userId));
                break;
            case ("CURRENT"):
                bookings.addAll(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc
                        (userId,LocalDateTime.now()));
                break;
            case ("FUTURE"):
                bookings.addAll(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()));
                break;
            case ("PAST"):
                bookings.addAll(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()));
                break;
            case ("WAITING"):
                bookings.addAll(bookingRepository.findByBookerIdAndStatusOrderByStartDesc
                        (userId, BookingStatus.WAITING));
                break;
            case ("REJECTED"):
                bookings.addAll(bookingRepository.findByBookerIdAndStatusOrderByStartDesc
                        (userId, BookingStatus.REJECTED));
                break;
            default:
                log.warn("некорректный статус: {}",state);
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingMapper.toBookingDto(bookings);
    }

    /**
     * Получние списка бронирования для всех предметов пользователя
     *
     * @param ownerId id пользователя
     * @param state   состояние бронирования
     */
    @Override
    public List<BookingDto> getBookingByOwnerId(long ownerId, String state) {
        validator.validateAndReturnUserByUserId(ownerId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ("ALL"):
                bookings.addAll(bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId));
                break;
            case ("CURRENT"):
                bookings.addAll(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc
                        (ownerId, LocalDateTime.now(), LocalDateTime.now()));
                break;
            case ("FUTURE"):
                bookings.addAll(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now()));
                break;
            case ("PAST"):
                bookings.addAll(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now()));
                break;
            case ("WAITING"):
                bookings.addAll(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc
                        (ownerId, BookingStatus.WAITING));
                break;
            case ("REJECTED"):
                bookings.addAll(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc
                        (ownerId, BookingStatus.REJECTED));
                break;
            default:
                log.warn("некорректный статус: {}",state);
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingMapper.toBookingDto(bookings);
    }

    /**
     * Добавление нового бронирования
     *
     * @param userId     id пользователя
     * @param bookingDto dto бронирования
     */
    @Override
    public BookingDto addBooking(long userId, BookingDtoOnlyId bookingDto) {
        User user = validator.validateAndReturnUserByUserId(userId);
        Item item = validator.validateAndReturnItemByItemId(bookingDto.getItemId());
        validator.validateForAddBooking(user, item, bookingDto);
        Booking booking = bookingMapper.newBooking(bookingDto, user, item, BookingStatus.WAITING);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    /**
     * Поиск бронирования по id
     *
     * @param userId    id пользователя
     * @param bookingId id бронирования
     */
    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        log.info("BookingServiceImpl.getBookingById userId {} bookingId {}", userId, bookingId);
        User user = validator.validateAndReturnUserByUserId(userId);
        BookingDto bookingDto = bookingMapper.toBookingDto(validator.validateForGetBooking(user, bookingId));
        log.info("BookingServiceImpl.getBookingById return bookingDto{}", bookingDto);
        return bookingDto;
    }

    /**
     * Обновление бронирования
     *
     * @param userId    id пользователя
     * @param bookingId id бронирования
     * @param approved  подтверждено бронирование или отклонено
     */
    @Override
    public BookingDto updateStatusBooking(long userId, long bookingId, Boolean approved) {
        User owner = validator.validateAndReturnUserByUserId(userId);
        Booking booking = validator.validateForUpdateBooking(owner, bookingId, approved);
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

//    @Override
//    public void validateBookingForComment(Item item, User booker, BookingStatus status) {
//        List<Booking> bookingList = bookingRepository.findByItemAndBookerAndStatusNot(item, booker, status);
//        if (bookingList.isEmpty()) {
//            log.warn("Вы не брали в аренду эту вещь");
//            throw new ValidationException("Вы не брали в аренду эту вещь");
//        }
//    }


//    private void validateForAddBooking(User user, Item item, BookingDtoOnlyId bookingDtoId) {
//        if (user.equals(item.getOwner())) {
//            log.warn("Владелец не может арендовать у себя");
//            throw new ItemNotFoundException("Владелец не может арендовать у себя");
//        }
//        if (!item.getAvailable()) {
//            log.warn("Вешь занята");
//            throw new ValidationException("Вешь занята");
//        }
//        LocalDateTime startTime = bookingDtoId.getStart();
//        LocalDateTime endTime = bookingDtoId.getEnd();
//        if (endTime.isBefore(LocalDateTime.now())) {
//            log.warn("Время окончания не корректно");
//            throw new ValidationException("Время окончания не корректно");
//        }
//        if (startTime.isAfter(endTime)) {
//            log.warn("Время окончания раньше начала");
//            throw new ValidationException("Время окончания раньше начала");
//        }
//        if (startTime.isBefore(LocalDateTime.now())) {
//            log.warn("Время начала не корректно");
//            throw new ValidationException("Время начала не корректно");
//        }
//    }
//
//    private Booking validateForGetBooking(User user, long bookingId) {
//        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
//                new ItemNotFoundException(String.format("бронь предмета с bookingId '%d' не найдена!", bookingId)));
//        if (user.equals(booking.getBooker()) || user.equals(booking.getItem().getOwner())) {
//            return booking;
//        } else {
//            log.warn("Вы не владелец или пользователь вещи");
//            throw new ItemNotFoundException("Вы не владелец или пользователь вещи");
//        }
//    }
//
//    private Booking validateForUpdateBooking(User owner, long bookingId, Boolean approved) {
//        Booking booking = validateForGetBooking(owner, bookingId);
//        if (approved == null) {
//            log.warn("Approved не может быть пустым");
//            throw new ValidationException("Approved не может быть пустым");
//        }
//        if (booking.getStatus() == BookingStatus.APPROVED) {
//            log.warn("Статус предмета уже изменен(APPROVED)");
//            throw new ValidationException("Статус предмета уже изменен(APPROVED)");
//        }
//        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
//            log.warn("Бронь уже подтверждена(WAITING)");
//            throw new ValidationException("Бронь уже подтверждена(WAITING)");
//        }
//        if (!booking.getItem().getOwner().equals(owner)) {
//            log.warn("Вы не владелец вещи");
//            throw new UserNotFoundException("Вы не владелец вещи");
//        }
//        return booking;
//    }
}