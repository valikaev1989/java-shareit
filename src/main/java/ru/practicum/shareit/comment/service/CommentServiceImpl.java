package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final Validator validator;

    /**
     * Получение списка отзывов предмета
     *
     * @param itemId id предмета
     */
    @Override
    public List<CommentDto> getCommentsByItemId(long itemId) {
        List<Comment> commentDtoList = commentRepository.getAllByItemId(itemId);
        return commentMapper.toCommentDto(commentDtoList);
    }

    /**
     * Добавление комментария к предмету
     *
     * @param userId     id пользователя
     * @param itemId     id предмета
     * @param commentDto dto комментария
     */
    @Override
    public CommentDto addCommentForItem(long userId, long itemId, CommentDto commentDto) {
        Item item = validator.validateAndReturnItemByItemId(itemId);
        User user = validator.validateAndReturnUserByUserId(userId);
        validator.validateBookingForComment(item, user, commentDto);
        Comment comment = commentRepository.save(commentMapper.toComment(commentDto, item, user));
        return commentMapper.toCommentDto(comment);
    }
}