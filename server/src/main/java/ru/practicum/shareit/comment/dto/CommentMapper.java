package ru.practicum.shareit.comment.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public Comment toComment(CommentDto commentDto, Item item, User user) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

    public List<CommentDto> toCommentDtoList(Collection<Comment> comments) {
        return comments.stream().map(this::toCommentDto).collect(Collectors.toList());
    }
}