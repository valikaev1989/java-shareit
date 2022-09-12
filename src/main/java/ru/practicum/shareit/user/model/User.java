package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
/**
 * id — уникальный идентификатор пользователя;
 * name — имя или логин пользователя;
 * email — адрес электронной почты.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    @Column(name = "email", nullable = false, length = 200)
    private String email;
}