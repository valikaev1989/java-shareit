package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Получение списка отзывов предмета
     *
     * @param itemId id предмета
     */
    @Query("select c from Comment c where c.item.id = ?1")
    List<Comment> getAllByItemId(long itemId);
}