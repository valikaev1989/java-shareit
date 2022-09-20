package ru.practicum.shareit.comment.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * id — уникальный идентификатор комментария;
 * item — вещь, к которой относится комментарий;
 * author — автор комментария;
 * text — содержимое комментария;
 * created — дата создания комментария.
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private long id;
    @JoinColumn(name = "item_id")
    @ManyToOne
    private Item item;
    @JoinColumn(name = "author_user_id")
    @ManyToOne
    private User author;
    @Column(name = "text")
    private String text;
    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();
}