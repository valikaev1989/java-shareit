package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    /**
     * Получение списка запросов пользователя
     */
    @Query("select i from ItemRequest i where i.requester.id = ?1")
    List<ItemRequest> findAllByRequesterId(Long requesterId);

    /**
     * Получение списка запросов других пользователей
     */
    @Query("select i from ItemRequest i where i.requester.id <> ?1")
    List<ItemRequest> findAllByRequesterIdIsNot(Long userId, Pageable pageable);
}