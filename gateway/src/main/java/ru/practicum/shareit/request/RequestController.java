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
        log.info("GATEWAY start addNewItemRequest: userId = {}, requestDto =  {}", userId, requestDto);
        validator.validateId(userId);
        validator.validateItemRequestDesc(requestDto);
        ResponseEntity<Object> responseEntity = requestClient.addRequest(userId, requestDto);
        log.info("GATEWAY end addNewItemRequest: request =  {}", responseEntity);
        return responseEntity;
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader(HEADER) long userId) {
        log.info("GATEWAY start getOwnRequests: userId = {}", userId);
        validator.validateId(userId);
        ResponseEntity<Object> responseEntity = requestClient.getOwnRequests(userId);
        log.info("GATEWAY end getOwnRequests: request =  {}", responseEntity);
        return responseEntity;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(HEADER) long userId,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("GATEWAY start getAllRequests: userId = {}, from = {} and size = {}", userId, from, size);
        validator.validateId(userId);
        validator.validatePage(from, size);
        int[] page = {from, size};
        ResponseEntity<Object> responseEntity = requestClient.getAllRequests(userId, page);
        log.info("GATEWAY end getAllRequests: request =  {}", responseEntity);
        return responseEntity;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(HEADER) long userId, @PathVariable long requestId) {
        log.info("GATEWAY start getRequest: userId =  {}, requestId =  {}", userId, requestId);
        validator.validateId(userId);
        validator.validateId(requestId);
        ResponseEntity<Object> responseEntity = requestClient.getRequest(userId, requestId);
        log.info("GATEWAY end getRequest: request =  {}", responseEntity);
        return responseEntity;
    }
}