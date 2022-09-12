package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;
/**
 * id — уникальный идентификатор вещи;
 * name — краткое название;
 * description — развёрнутое описание;
 * available — статус о том, доступна или нет вещь для аренды;
 * lastBooking — последнее бронирование;
 * nextBooking - следующее бронирование;
 * comments - отзывы;
 */
@Data
@Builder(toBuilder = true)
public class ItemOwnerDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoOnlyId lastBooking;
    private BookingDtoOnlyId nextBooking;
    List<CommentDto> comments;
}