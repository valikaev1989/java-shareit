package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * id — уникальный идентификатор комментария;
 * text — содержимое комментария;
 * authorName — автор комментария;
 * created — дата создания комментария.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private long id;
    private String text;
    private String authorName;
    private Instant created = Instant.now();
}