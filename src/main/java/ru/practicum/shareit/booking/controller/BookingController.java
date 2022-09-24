package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingDto> getAllBookingsFromUser(
            @RequestHeader(HEADER) long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("UserBooker {} get own bookings state = {}", userId, state);
        log.info("With from = {} and size = {}", from, size);
        return bookingService.getBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByIdOwner(
            @RequestHeader(HEADER) long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("UserOwner {} get bookings for items state = {}", userId, state);
        log.info("With from = {} and size = {}", from, size);
        return bookingService.getBookingsByOwnerId(userId, state, from, size);
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader(HEADER) long userId, @RequestBody BookingDtoOnlyId bookingDto) {
        log.info("User {} create booking {}", userId, bookingDto);
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatusBooking(@RequestHeader(HEADER) long userId,
                                          @RequestParam Boolean approved, @PathVariable long bookingId) {
        log.info("User {} updated booking {} set approval = {}", userId, bookingId, approved);
        return bookingService.updateStatusBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(HEADER) long userId, @PathVariable long bookingId) {
        log.info("User {} get booking id = {}", userId, bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }
}