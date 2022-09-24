package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Получение списка предметов пользователя
     *
     * @param userId   id пользователя
     * @param pageable
     */
    @Query("select i from Item i where i.owner.id = ?1 order by i.id")
    List<Item> findByOwnerIdOrderById(long userId, Pageable pageable);

    /**
     * Поиск предметов по тексту в названии и описании.
     *
     * @param text     текст для поиска
     * @param pageable сортировка по количеству страниц и количеству выборки на странице
     */
    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')) " +
            "and i.available = true")
    List<Item> searchItemByNameAndDesc(String text, Pageable pageable);

    /**
     * Получение списка предметов по идентификатору запроса предмета
     *
     * @param requestId id запроса предмета
     */
    @Query("select i from Item i where i.requestId = ?1")
    List<Item> findByRequestId(Long requestId);
}