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
    public List<BookingDto> getAllBookingsFromUser(@RequestHeader(HEADER) long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("User {} get own bookings state = {}", userId, state);
        return bookingService.getAllBookingsFromUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByIdOwner(@RequestHeader(HEADER) long userId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        log.info("User {} get bookings for items state = {}", userId, state);
        return bookingService.getBookingByIdOwner(userId, state);
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