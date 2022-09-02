package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;

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