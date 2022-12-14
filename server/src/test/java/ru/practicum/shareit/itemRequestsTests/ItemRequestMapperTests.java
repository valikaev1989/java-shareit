package ru.practicum.shareit.itemRequestsTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestMapperTests extends StorageForTests {
    private final ItemRequestMapper itemRequestMapper;

    @Test
    @DisplayName("Тест маппинга в Request")
    void toRequest() {
        ItemRequest expectedItemRequest = createRequest();
        ItemRequest actualItemRequest = itemRequestMapper.toItemRequest(createUserTwo(), createRequestDto());
        actualItemRequest.setId(1);
        assertEquals(expectedItemRequest.toString(), actualItemRequest.toString());
    }

    @Test
    @DisplayName("Тест маппинга в RequestDto")
    void toRequestDto() {
        ItemRequestDto expectedItemRequestDto = createRequestDto();
        ItemRequestDto actualItemRequestDto = itemRequestMapper.toItemRequestDto(createRequest());
        assertEquals(expectedItemRequestDto, actualItemRequestDto);
    }

    @Test
    @DisplayName("Тест маппинга в RequestWithItemDto")
    void toRequestWithItemDto() {
        ItemRequestWithItemDto expectedResult = createRequestWithItemDto();
        expectedResult.setItems(List.of(createItemDtoWithRequestId()));
        ItemRequestWithItemDto actualResult =
                itemRequestMapper.toItemRequestWithItemDto(createRequest(), List.of(createItemDtoWithRequestId()));
        assertEquals(expectedResult, actualResult);
    }
}