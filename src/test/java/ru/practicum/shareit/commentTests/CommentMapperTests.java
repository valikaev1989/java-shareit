package ru.practicum.shareit.commentTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentMapperTests extends StorageForTests {
    private final CommentMapper commentMapper;

    @Test
    void toCommentDto() {
        Comment comment = createComment();
        CommentDto expectedCommentDto = createCommentDto();
        CommentDto actualCommentDto = commentMapper.toCommentDto(comment);
        assertEquals(expectedCommentDto, actualCommentDto);
    }

    @Test
    void toComment() {
        CommentDto commentDto = createCommentDto();
        User author = createUserTwo();
        Item item = createItemNullRequest();
        Comment expectedComment = createComment();
        Comment actualComment = commentMapper.toComment(commentDto, item, author);
        assertEquals(expectedComment.toString(), actualComment.toString());
    }

    @Test
    void toCommentDtoList(){
        Comment comment = createComment();
        List<CommentDto> expectedCommentDtoList= List.of(createCommentDto());
        List<CommentDto> actualCommentDtoList = commentMapper.toCommentDtoList(List.of(comment));
        assertEquals(expectedCommentDtoList,actualCommentDtoList);
        assertEquals(createCommentDto(),actualCommentDtoList.get(0));
    }
}
