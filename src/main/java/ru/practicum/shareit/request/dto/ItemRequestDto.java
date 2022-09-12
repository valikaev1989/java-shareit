package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * id — уникальный идентификатор запроса;
 * description — текст запроса, содержащий описание требуемой вещи;
 * requester — пользователь, создавший запрос;
 * created — дата и время создания запроса.
 * items - список вещей по данному запросу
 */
@Data
@Builder(toBuilder = true)
public class ItemRequestDto {
    private long id;
    private String description;
    private long requester;
    private LocalDateTime created;
}