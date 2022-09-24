package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequestId()
        );
    }

    public Item toItem(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public ItemOwnerDto toItemOwnerDto(Item item, List<CommentDto> comments,
                                       BookingDtoOnlyId last, BookingDtoOnlyId next) {
        return new ItemOwnerDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), last, next, comments);
    }

    public List<ItemDto> toItemDtoList(Collection<Item> items) {
        return items.stream().map(this::toItemDto).collect(Collectors.toList());
    }
}