package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemOwnerDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    BookingDtoOnlyId lastBooking;
    BookingDtoOnlyId nextBooking;
    List<CommentDto> comments;
}