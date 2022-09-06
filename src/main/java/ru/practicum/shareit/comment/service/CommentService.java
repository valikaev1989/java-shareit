package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {
    /**
     * Получение списка отзывов предмета
     */
    List<CommentDto> getCommentsByItemId(long itemId);

    /**
     * Добавление комментария к предмету
     */
    CommentDto addCommentForItem(long userId, long itemId, CommentDto commentDto);
}