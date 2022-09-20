package ru.practicum.shareit.commentTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.service.CommentServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CommentServiceTests extends StorageForTests {
    private CommentServiceImpl mockCommentServiceImpl;
    @Mock
    private CommentRepository mockCommentRepository;
    @Autowired
    private CommentMapper commentMapper;
    @Mock
    private Validator mockValidator;

    @BeforeEach
    void setUp() {
        mockCommentServiceImpl = new CommentServiceImpl(mockCommentRepository, commentMapper, mockValidator);
    }

    @Test
    void getCommentsByItemId() {
        Item item = createItemNullRequest();
        Comment comment = createComment();
        CommentDto commentDto = createCommentDto2();
        List<CommentDto> expectedCommentDto = List.of(commentDto);
        when(mockCommentRepository.getAllByItemId(anyLong())).thenReturn(List.of(comment));
        List<CommentDto> actualCommentDto = mockCommentServiceImpl.getCommentsByItemId(item.getId());
        assertEquals(expectedCommentDto, actualCommentDto);
    }

    @Test
    void addCommentForItem() {
        User user = createUserTwo();
        Item item = createItemNullRequest();
        Comment comment = createComment();
        CommentDto expectedCommentDto = createCommentDto2();
        CommentDto commentDto1 = createCommentDtoWithoutId();
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockCommentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto actualCommentDto = mockCommentServiceImpl.addCommentForItem(
                user.getId(), item.getId(), commentDto1);
        assertEquals(expectedCommentDto, actualCommentDto);
        assertEquals(expectedCommentDto.getAuthorName(), actualCommentDto.getAuthorName());
    }
}