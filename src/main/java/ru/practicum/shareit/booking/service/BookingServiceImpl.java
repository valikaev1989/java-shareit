package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
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
    public List<BookingDto> getBookingsByBookerId(long userId, String state) {
        validator.validateAndReturnUserByUserId(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ("ALL"):
                bookings.addAll(bookingRepository.findAllByBookerId(userId));
                break;
            case ("CURRENT"):
                bookings.addAll(bookingRepository.findCurrentBookingByBookerId(userId, LocalDateTime.now()));
                break;
            case ("FUTURE"):
                bookings.addAll(bookingRepository.findFutureBookingByBookerId(userId, LocalDateTime.now()));
                break;
            case ("PAST"):
                bookings.addAll(bookingRepository.findPastBookingByBookerId(userId, LocalDateTime.now()));
                break;
            case ("WAITING"):
                bookings.addAll(bookingRepository.findByBookerIdAndStatus
                        (userId, BookingStatus.WAITING));
                break;
            case ("REJECTED"):
                bookings.addAll(bookingRepository.findByBookerIdAndStatus
                        (userId, BookingStatus.REJECTED));
                break;
            default:
                log.warn("некорректный статус: {}", state);
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingMapper.toBookingDto(bookings);
    }

    /**
     * Получение списка бронирования для всех предметов пользователя
     *
     * @param ownerId id пользователя
     * @param state   состояние бронирования
     */
    @Override
    public List<BookingDto> getBookingsByOwnerId(long ownerId, String state) {
        validator.validateAndReturnUserByUserId(ownerId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ("ALL"):
                bookings.addAll(bookingRepository.findAllByItemOwnerId(ownerId));
                break;
            case ("CURRENT"):
                bookings.addAll(bookingRepository.findCurrentBookingByItemOwnerId
                        (ownerId, LocalDateTime.now()));
                break;
            case ("FUTURE"):
                bookings.addAll(bookingRepository.findFutureBookingByItemOwnerId(ownerId, LocalDateTime.now()));
                break;
            case ("PAST"):
                bookings.addAll(bookingRepository.findPastBookingByItemOwnerId(ownerId, LocalDateTime.now()));
                break;
            case ("WAITING"):
                bookings.addAll(bookingRepository.findBookingByOwnerIdAndStatus
                        (ownerId, BookingStatus.WAITING));
                break;
            case ("REJECTED"):
                bookings.addAll(bookingRepository.findBookingByOwnerIdAndStatus
                        (ownerId, BookingStatus.REJECTED));
                break;
            default:
                log.warn("некорректный статус: {}", state);
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
        User user = validator.validateAndReturnUserByUserId(userId);
        return bookingMapper.toBookingDto(validator.validateForGetBooking(user, bookingId));
    }

    /**
     * Обновление бронирования
     *
     * @param userId    id пользователя
     * @param bookingId id бронирования
     * @param approved  подтверждение бронирования
     */
    @Override
    public BookingDto updateStatusBooking(long userId, long bookingId, Boolean approved) {
        User owner = validator.validateAndReturnUserByUserId(userId);
        Booking booking = validator.validateForUpdateBooking(owner, bookingId, approved);
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }
}