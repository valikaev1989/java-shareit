package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper,@Lazy
                              ItemService itemService, UserService userService, BookingService bookingService) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.itemService = itemService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    /**
     * Получение списка отзывов предмета
     *
     * @param itemId id предмета
     */
    @Override
    public List<CommentDto> getCommentsByItemId(long itemId) {
        return commentMapper.toCommentDto(commentRepository.getAllByItemId(itemId));
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
        Item item = itemService.findItemById(itemId);
        User user = userService.findUserById(userId);
        validateCommentText(commentDto.getText());
        bookingService.validateBookingForComment(item,user, BookingStatus.REJECTED);
        Comment comment = commentRepository.save(commentMapper.toComment(commentDto, item, user));
        return commentMapper.toCommentDto(comment);
    }
    /**
     * проверка пустого комментария
     *
     * @param commentText текст комментария
     */
    private void validateCommentText(String commentText) {
        if (commentText.isEmpty()) {
            log.warn("Комментарий не должен быть пустым!");
            throw new ValidationException("Комментарий не должен быть пустым!");
        }
    }
}