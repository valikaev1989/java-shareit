package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String HEADER = "X-Sharer-User-Id";

    private final ItemRequestService requestService;

    @GetMapping
    public List<ItemRequestWithItemDto> getOwnRequests(@RequestHeader(HEADER) long userId) {
        log.info("User {} get own requests", userId);
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemDto> getAllRequests(@RequestHeader(HEADER) long userId,
                                                       @RequestParam(value = "from", required = false,
                                                               defaultValue = "0") int from,
                                                       @RequestParam(value = "size", required = false,
                                                               defaultValue = "10") int size) {
        log.info("User {} get all requests with from = {} and size = {}", userId, from, size);
        return requestService.getAllRequests(userId, from, size);
    }

    @PostMapping
    public ItemRequestDto addNewItemRequest(@RequestHeader(HEADER) long userId,
                                            @RequestBody ItemRequestDto requestDto) {
        log.info("User {} create new ItemRequest {}", userId, requestDto);
        return requestService.addItemRequest(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemDto getRequest(@RequestHeader(HEADER) long userId, @PathVariable long requestId) {
        log.info("User {} get request {}", userId, requestId);
        return requestService.getRequest(userId, requestId);
    }
}