package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;

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