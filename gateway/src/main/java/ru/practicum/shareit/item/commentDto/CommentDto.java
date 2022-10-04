package ru.practicum.shareit.item.commentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private long id;
    private String text;
    private String authorName;
}