package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.commentDto.CommentDto;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.util.ValidatorGateway;

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
    private final ValidatorGateway validator;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto) {
        log.info("GATEWAY: User {} create item {}", userId, itemDto);
        validator.validateId(userId);
        validator.validateItemAll(itemDto);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto,
                                             @PathVariable long itemId) {
        log.info("GATEWAY: User {} update item {} with {}", userId, itemId, itemDto);
        validator.validateId(userId);
        validator.validateId(itemId);
        validator.validateItemNameAndDescOnEmpty(itemDto);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        log.info("GATEWAY: Get item id = {}", itemId);
        validator.validateId(userId);
        validator.validateId(itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByDescription(@RequestParam(value = "text") String text,
                                                    @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                    @Positive @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("GATEWAY: Get item name/description = {}", text);
        validator.validatePage(from, size);
        return itemClient.findByDescription(text, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(HEADER) long userId,
                                                  @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                  @Positive @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("GATEWAY: Get items by user id = {}", userId);
        validator.validateId(userId);
        validator.validatePage(from, size);
        return itemClient.findAllByUserId(userId, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        log.info("GATEWAY: Delete item id = {}", itemId);
        validator.validateId(userId);
        validator.validateId(itemId);
        itemClient.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER) long userId, @PathVariable long itemId,
                                             @RequestBody CommentDto commentDto) {
        log.info("GATEWAY: User {} adds comment {} to item {}", userId, commentDto, itemId);
        validator.validateId(userId);
        validator.validateId(itemId);
        validator.validateCommentText(commentDto);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}