package ru.practicum.shareit.item.dto;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public Item toItem(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
    }

    public List<ItemDto> toItemDto(Collection<Item> items) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : items) {
            itemDtoList.add(toItemDto(item));
        }
        return itemDtoList;
    }
}