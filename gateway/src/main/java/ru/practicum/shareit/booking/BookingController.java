package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

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

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(HEADER) long userId,
                                             @RequestBody BookingDto bookingDto) {
        log.info("GATEWAY start addBooking: bookingDto =  {}, userId = {}", bookingDto, userId);
        ResponseEntity<Object> responseEntity = bookingClient.addBooking(userId, bookingDto);
        log.info("GATEWAY end addBooking: booking =  {}", responseEntity);
        return responseEntity;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatusBooking(@RequestHeader(HEADER) long userId,
                                                      @RequestParam Boolean approved,
                                                      @PathVariable Long bookingId) {
        log.info("GATEWAY start updateStatus: bookingId = {}, userId = {}, approved = {}",
                bookingId, userId, approved);
        ResponseEntity<Object> responseEntity = bookingClient.updateStatusBooking(userId, bookingId, approved);
        log.info("GATEWAY end updateStatus: booking = {}", responseEntity);
        return responseEntity;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(HEADER) long userId,
                                                 @PathVariable Long bookingId) {
        log.info("GATEWAY start getBookingById: bookingId = {}, userId = {}", bookingId, userId);
        ResponseEntity<Object> responseEntity = bookingClient.getBookingById(userId, bookingId);
        log.info("GATEWAY end getBookingById: booking = {}", responseEntity);
        return responseEntity;
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsFromUser(
            @RequestHeader(HEADER) long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GATEWAY getAllBookingsFromUser: state = {}, userId = {}, from = {}, size = {}",
                stateParam, userId, from, size);
        int[] page = {from, size};
        ResponseEntity<Object> responseEntity = bookingClient.getAllBookingsFromUser(userId, stateParam, page);
        log.info("GATEWAY end getAllBookingsFromUser: booking = {}", responseEntity);
        return responseEntity;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByIdOwner(
            @RequestHeader(HEADER) long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GATEWAY getBookingByIdOwner: state = {}, userId = {}, from = {}, size = {}",
                stateParam, userId, from, size);
        int[] page = {from, size};
        ResponseEntity<Object> responseEntity = bookingClient.getBookingByIdOwner(userId, stateParam, page);
        log.info("GATEWAY end getBookingByIdOwner: booking = {}", responseEntity);
        return responseEntity;
    }
}