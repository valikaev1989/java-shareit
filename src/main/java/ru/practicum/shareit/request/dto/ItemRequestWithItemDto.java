package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * id — уникальный идентификатор запроса;
 * description — текст запроса, содержащий описание требуемой вещи;
 * created — дата и время создания запроса.
 * items - список вещей по данному запросу
 */
@Data
@Builder(toBuilder = true)
public class ItemRequestWithItemDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}