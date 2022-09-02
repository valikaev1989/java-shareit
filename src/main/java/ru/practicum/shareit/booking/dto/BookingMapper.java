package ru.practicum.shareit.booking.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Component
public class BookingMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Autowired
    public BookingMapper(UserMapper userMapper, ItemMapper itemMapper) {
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemMapper.toItemDto(booking.getItem()),
                userMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public Booking toBooking(BookingDto bookingDto, User user, Item item, BookingStatus status) {
        return new Booking(
                bookingDto.getId(),
                item,
                user,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                status
        );

    }
    public Booking newBooking(BookingDtoOnlyId bookingDto, User user, Item item, BookingStatus status) {
        return new Booking(
                bookingDto.getId(),
                item,
                user,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                status
        );

    }

    public List<BookingDto> toBookingDto(Collection<Booking> bookings) {
        List<BookingDto> bookingDtoList = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtoList.add(toBookingDto(booking));
        }
        return bookingDtoList;
    }
}