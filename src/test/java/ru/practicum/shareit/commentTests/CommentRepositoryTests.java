package ru.practicum.shareit.commentTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentRepositoryTests extends StorageForTests {
    private final TestEntityManager entityManager;
    private final CommentRepository commentRepository;

    @Test
    @DisplayName("DataJpaTest получения всех комментариев о предмете")
    void getAllCommentByItemId() {
        User user = createUserWithoutId();
        entityManager.persist(user);
        Item item = createItemWithoutId(user);
        entityManager.persist(item);
        Comment comment = createCommentWithoutId(user, item);
        entityManager.persist(comment);
        List<Comment> expectedList = List.of(comment);
        List<Comment> actualList = commentRepository.getAllByItemId(item.getId());
        assertEquals(expectedList, actualList);
        assertEquals(comment, actualList.get(0));
    }
}