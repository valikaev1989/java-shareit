package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemOwnerDto> findAllByUserId(@RequestHeader(HEADER) long userId) {
        log.info("Get items by user id = {}", userId);
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByDescription(@RequestParam(value = "text") String text) {
        log.info("search items by text = {} in name and description", text);
        return itemService.searchItemByNameAndDesc(text);
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
    public ItemDto findById(@PathVariable long itemId) {
        log.info("Get item id = {}", itemId);
        return itemService.findItemDtoById(itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        log.info("Delete item id = {}", itemId);
        itemService.deleteItemById(userId, itemId);
    }
}