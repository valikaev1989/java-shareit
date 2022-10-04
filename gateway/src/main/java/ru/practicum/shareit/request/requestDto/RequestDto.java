package ru.practicum.shareit.request.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private long id;
    private String description;
    private Long requester;
}