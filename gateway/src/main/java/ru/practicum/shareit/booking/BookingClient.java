package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.util.ValidatorGateway;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";
    private final ValidatorGateway validator;


    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder,
                         ValidatorGateway validator) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
        this.validator = validator;
    }

    public ResponseEntity<Object> getAllBookingsFromUser(long userId, String stateParam, int[] page) {
        validator.validateId(userId);
        validator.validatePage(page[0], page[1]);
        BookingState state = validator.validateStateBooking(stateParam);
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", page[0],
                "size", page[1]
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingByIdOwner(long userId, String stateParam, int[] page) {
        validator.validateId(userId);
        validator.validatePage(page[0], page[1]);
        BookingState state = validator.validateStateBooking(stateParam);
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", page[0],
                "size", page[1]
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addBooking(long userId, BookingDto bookingDto) {
        validator.validateId(userId);
        validator.validateTimeBooking(bookingDto);
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> getBookingById(long userId, Long bookingId) {
        validator.validateId(userId);
        validator.validateId(bookingId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> updateStatusBooking(long userId, Long bookingId, Boolean approved) {
        validator.validateId(userId);
        validator.validateId(bookingId);
        validator.validateApprovedBooking(approved);
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }
}