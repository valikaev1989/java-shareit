package ru.practicum.shareit.itemRequestsTests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;

import java.util.List;

@JsonTest
public class ItemRequestDtoJsonTests extends StorageForTests {
    @Autowired
    private JacksonTester<ItemRequestDto> json;
    @Autowired
    private JacksonTester<ItemRequestWithItemDto> json2;

    @Test
    @DisplayName("Тест json ItemRequestDto")
    void jsonItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = createRequestDto();
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) itemRequestDto.getId());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.requesterId")
                .isEqualTo((int) itemRequestDto.getRequesterId());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDto.getCreated().toString());
    }

    @Test
    @DisplayName("Тест json ItemRequestWithItemDto")
    void jsonItemRequestWithItemDto() throws Exception {
        ItemRequestWithItemDto itemRequestWithItemDto = createRequestWithItemDto();
        JsonContent<ItemRequestWithItemDto> result = json2.write(itemRequestWithItemDto);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) itemRequestWithItemDto.getId());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestWithItemDto.getDescription());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestWithItemDto.getCreated().toString());
        Assertions.assertThat(result).extractingJsonPathArrayValue("items")
                .isEqualTo(List.of());
    }
}