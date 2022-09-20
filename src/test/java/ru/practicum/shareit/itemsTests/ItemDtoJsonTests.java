package ru.practicum.shareit.itemsTests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.ArrayList;
import java.util.List;

@JsonTest
public class ItemDtoJsonTests extends StorageForTests {
    @Autowired
    private JacksonTester<ItemDto> json;
    @Autowired
    private JacksonTester<ItemOwnerDto> json2;

    @Test
    void jsonItemDto() throws Exception {
        ItemDto itemDto = createItemDtoWithRequestId();
        JsonContent<ItemDto> result = json.write(itemDto);
        System.out.println(itemDto);
        System.out.println(result);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) itemDto.getId());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        Assertions.assertThat(result).extractingJsonPathValue("$.available")
                .isEqualTo(itemDto.getAvailable());
        Assertions.assertThat(result).extractingJsonPathValue("$.ownerId")
                .isEqualTo((int) itemDto.getOwnerId());
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(Math.toIntExact(itemDto.getRequestId()));
    }

    @Test
    void jsonItemOwnerDto1() throws Exception {
        ItemOwnerDto itemOwnerDto = createItemOwnerDto();
        itemOwnerDto.setLastBooking(null);
        itemOwnerDto.setNextBooking(null);
        itemOwnerDto.setComments(List.of());
        JsonContent<ItemOwnerDto> result = json2.write(itemOwnerDto);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) itemOwnerDto.getId());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemOwnerDto.getName());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemOwnerDto.getDescription());
        Assertions.assertThat(result).extractingJsonPathValue("$.available")
                .isEqualTo(itemOwnerDto.getAvailable());
        Assertions.assertThat(result).extractingJsonPathValue("lastBooking")
                .isEqualTo(null);
        Assertions.assertThat(result).extractingJsonPathValue("nextBooking")
                .isEqualTo(null);
        Assertions.assertThat(result).extractingJsonPathArrayValue("comments")
                .isEqualTo(List.of());
    }
}