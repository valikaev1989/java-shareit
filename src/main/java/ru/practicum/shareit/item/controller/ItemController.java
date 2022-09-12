package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final CommentService commentService;

    @Autowired
    public ItemController(ItemService itemService, CommentService commentService) {
        this.itemService = itemService;
        this.commentService = commentService;
    }

    @GetMapping
    public List<ItemOwnerDto> findAllByUserId(@RequestHeader(HEADER) long userId,
                                              @RequestParam(value = "from", required = false,
                                                      defaultValue = "0") int from,
                                              @RequestParam(value = "size", required = false,
                                                      defaultValue = "10") int size) {
        log.info("Get items by user id = {}", userId);
        log.info("With from = {} and size = {}", from, size);
        return itemService.getAllUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemByText(@RequestParam(value = "text") String text,
                                        @RequestParam(value = "from", required = false,
                                                defaultValue = "0") int from,
                                        @RequestParam(value = "size", required = false,
                                                defaultValue = "10") int size) {
        log.info("search items by text = {} in name and description", text);
        log.info("With from = {} and size = {}", from, size);
        return itemService.findItemsByText(text, from, size);
    }


    @PostMapping
    public ItemDto addNewItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto) {
        log.info("User {} create item {}", userId, itemDto);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        log.info("User {} update item {} with {}", userId, itemId, itemDto);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemOwnerDto findItemById(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        log.info("Get itemId = {}, with userId = {}", itemId, userId);
        return itemService.findItemOwnerDtoById(userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        log.info("Delete item id = {}", itemId);
        itemService.deleteItemById(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HEADER) long userId, @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("User {} adds comment {} to item {}", userId, commentDto, itemId);
        return commentService.addCommentForItem(userId, itemId, commentDto);
    }
}