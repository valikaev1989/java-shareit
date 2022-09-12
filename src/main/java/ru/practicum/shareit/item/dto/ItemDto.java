package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
/**
 * id — уникальный идентификатор вещи;
 * name — краткое название;
 * description — развёрнутое описание;
 * available — статус о том, доступна или нет вещь для аренды;
 * requestId - id ссылки на соответствующий запрос
 */
@Data
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private long ownerId;
    private Long requestId;
}