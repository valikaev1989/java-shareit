package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.requestDto.RequestDto;
import ru.practicum.shareit.util.ValidatorGateway;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;
    private final ValidatorGateway validator;

    @PostMapping
    public ResponseEntity<Object> addNewItemRequest(@RequestHeader(HEADER) long userId,
                                                    @RequestBody RequestDto requestDto) {
        log.info("GATEWAY: User {} create new ItemRequest {}", userId, requestDto);
        validator.validateId(userId);
        validator.validateItemRequestDesc(requestDto);
        return requestClient.addRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader(HEADER) long userId) {
        log.info("GATEWAY: User {} get own requests", userId);
        validator.validateId(userId);
        return requestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(HEADER) long userId,
                                                 @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                 @Positive @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("GATEWAY: User {} get all requests with from = {} and size = {}", userId, from, size);
        validator.validateId(userId);
        validator.validatePage(from, size);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(HEADER) long userId, @PathVariable long requestId) {
        log.info("GATEWAY: User {} get request {}", userId, requestId);
        validator.validateId(userId);
        validator.validateId(requestId);
        return requestClient.getRequest(userId, requestId);
    }
}