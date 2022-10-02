package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.commentDto.CommentDto;
import ru.practicum.shareit.item.itemDto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto) {
        log.info("GATEWAY start addItem: userId = {}, itemDto = {}", userId, itemDto);
        ResponseEntity<Object> responseEntity = itemClient.addNewItem(userId, itemDto);
        log.info("GATEWAY end addItem: item = {}", responseEntity);
        return responseEntity;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto,
                                             @PathVariable long itemId) {
        log.info("GATEWAY start updateItem: userId = {}, itemId = {}, itemDto {}", userId, itemId, itemDto);
        ResponseEntity<Object> responseEntity = itemClient.updateItem(userId, itemId, itemDto);
        log.info("GATEWAY end updateItem: item = {}", responseEntity);
        return responseEntity;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        log.info("GATEWAY start findById: userId = {}, itemId = {}", userId, itemId);
        ResponseEntity<Object> responseEntity = itemClient.findItemById(userId, itemId);
        log.info("GATEWAY end findById: item = {}", responseEntity);
        return responseEntity;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemByText(
            @RequestHeader(HEADER) Long userId,
            @RequestParam(value = "text") String text,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("GATEWAY start findItemByText: text = {}, from={}, size={}", text, from, size);
        int[] page = {from, size};
        ResponseEntity<Object> responseEntity = itemClient.findItemByText(text, userId, page);
        log.info("GATEWAY end findItemByText: items = {}", responseEntity);
        return responseEntity;
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(
            @RequestHeader(HEADER) long userId,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("GATEWAY start findAllByUserId: user id = {} from={}, size={}", userId, from, size);
        int[] page = {from, size};
        ResponseEntity<Object> responseEntity = itemClient.findAllByUserId(userId, page);
        log.info("GATEWAY end findAllByUserId: items = {}", responseEntity);
        return responseEntity;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        log.info("GATEWAY start deleteItem: itemId = {}, userId = {}", itemId, userId);
        itemClient.deleteItem(userId, itemId);
        log.info("GATEWAY end deleteItem: item = {}", itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER) long userId, @PathVariable long itemId,
                                             @RequestBody CommentDto commentDto) {
        log.info("GATEWAY start addComment: userId = {}, comment = {}, itemId = {}", userId, commentDto, itemId);
        ResponseEntity<Object> responseEntity = itemClient.addComment(userId, itemId, commentDto);
        log.info("GATEWAY end addComment: comment = {}", responseEntity);
        return responseEntity;
    }
}