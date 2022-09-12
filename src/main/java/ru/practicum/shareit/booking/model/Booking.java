package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * id — уникальный идентификатор бронирования;
 * item — вещь, которую пользователь бронирует;
 * booker — пользователь, который осуществляет бронирование;
 * start — дата начала бронирования;
 * end — дата конца бронирования;
 * status — статус бронирования;
 */
@Data
@Entity
@Table(name = "bookings")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private long id;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_user_id")
    private User booker;
    @Column(name = "start_date_time")
    private LocalDateTime start;
    @Column(name = "end_date_time")
    private LocalDateTime end;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;
}