package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.ValidatorGateway;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;
    private final ValidatorGateway validator;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(HEADER) long userId,
                                             @RequestBody BookingDto bookingDto) {
        log.info("GATEWAY: Creating booking {}, userId={}", bookingDto, userId);
        validator.validateId(userId);
        validator.validateTimeBooking(bookingDto);
        return bookingClient.bookItem(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader(HEADER) long userId,
                                         @RequestParam Boolean approved, @PathVariable Long bookingId) {
        log.info("GATEWAY: Patch booking {}, userId={}, approved={}", bookingId, userId, approved);
        validator.validateId(userId);
        validator.validateId(bookingId);
        validator.validateApprovedBooking(approved);
        return bookingClient.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("GATEWAY: Get booking {}, userId={}", bookingId, userId);
        validator.validateId(userId);
        validator.validateId(bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(HEADER) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("GATEWAY: Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        validator.validateId(userId);
        validator.validatePage(from, size);
        BookingState state = validator.validateStateBooking(stateParam);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForItems(@RequestHeader(HEADER) long userId,
                                                      @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GATEWAY: Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        validator.validateId(userId);
        validator.validatePage(from, size);
        BookingState state = validator.validateStateBooking(stateParam);
        return bookingClient.getBookingsForItems(userId, state, from, size);
    }
}