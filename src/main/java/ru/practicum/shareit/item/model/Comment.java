package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.Instant;

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
    @ManyToOne
    private Item item;
    @JoinColumn(name = "author_user_id")
    @ManyToOne
    private User author;
    @Column(name = "text")
    private String text;
    @JoinColumn(name = "item_id")
    @Column(name = "created")
    private Instant created = Instant.now();
}