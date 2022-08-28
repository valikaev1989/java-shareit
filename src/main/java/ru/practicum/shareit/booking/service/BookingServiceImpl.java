package ru.practicum.shareit.booking.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(UserService userService, ItemService itemService,
                              BookingMapper bookingMapper, BookingRepository bookingRepository) {
        this.userService = userService;
        this.itemService = itemService;
        this.bookingMapper = bookingMapper;
        this.bookingRepository = bookingRepository;
    }

    /**
     * @param userId     id пользователя
     * @param bookingDto dto бронирования
     */
    @Override
    public BookingDto addBooking(long userId, BookingDtoOnlyId bookingDto) {
        return null;
    }

    /**
     * @param userId
     * @param bookingId
     * @param approved
     * @return
     */
    @Override
    public BookingDto updateStatusBooking(long userId, long bookingId, Boolean approved) {
        return null;
    }

    /**
     * @param userId
     * @param bookingId
     * @return
     */
    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        return null;
    }

    /**
     * @param userId
     * @param state
     * @return
     */
    @Override
    public List<BookingDto> getAllBookingsFromUser(long userId, String state) {
        return null;
    }

    /**
     * @param userId
     * @param state
     * @return
     */
    @Override
    public List<BookingDto> getBookingByIdOwner(long userId, String state) {
        return null;
    }

    private void checkItem(Long userId, Long itemId) {
        User user = userService.findUserById(userId);
        Item item = itemService.getItemById(itemId);
        if (user.equals(item.getOwner())) throw new NotFoundException("Владелец не может арендовать у себя");
    }
}