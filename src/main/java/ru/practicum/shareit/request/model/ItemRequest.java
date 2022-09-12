package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * id — уникальный идентификатор запроса;
 * description — текст запроса, содержащий описание требуемой вещи;
 * requester — пользователь, создавший запрос;
 * created — дата и время создания запроса..
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "item_requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_request_id")
    private long id;
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "requester_user_id")
    @ManyToOne
    private User requester;
    @Column(name = "created")
    private LocalDateTime created;
}