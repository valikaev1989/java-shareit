package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Получение списка предметов пользователя
     *
     * @param userId id пользователя
     */
    @Query("select i from Item i where i.owner.id = ?1 order by i.id")
    List<Item> findByOwnerIdOrderById(long userId);

    /**
     * Поиск предметов по тексту в названии и описании.
     *
     * @param name        текст для поиска
     * @param description текст для поиска
     */
    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?2, '%')) " +
            "and i.available = true")
    List<Item> searchItemByNameAndDesc(String name, String description);
}