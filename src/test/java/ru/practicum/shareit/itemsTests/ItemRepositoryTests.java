package ru.practicum.shareit.itemsTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTests extends StorageForTests {
    private final TestEntityManager entityManager;
    private final ItemRepository itemRepository;

    @Test
    @DisplayName("Поиск вещей по тексту")
    void getItemsByText() {
        User user1 = createUserWithoutId();
        entityManager.persist(user1);
        Item item1 = makeItem("Магнит", "сувенир", true, user1);
        entityManager.persist(item1);
        Item item2 = makeItem("Магний", "сувенир2", false, user1);
        entityManager.persist(item2);
        Item item3 = makeItem("магнитуда", "сувенир3", true, user1);
        entityManager.persist(item3);
        Item item4 = makeItem("Открытка", "сувенир4", true, user1);
        entityManager.persist(item4);
        Item item5 = makeItem("Сувенир", "магнит5", true, user1);
        entityManager.persist(item5);
        PageRequest pr = PageRequest.of(0, 20);
        List<Item> items = itemRepository.searchItemByNameAndDesc("Магни", pr);
        assertThat(items).hasSize(4).contains(item1, item2, item3, item5);
    }

    @Test
    @DisplayName("Получение списка предметов пользователя")
    void getItemsByOwnerId() {
        User user1 = createUserWithoutId();
        entityManager.persist(user1);
        User user2 = createUserTwoWithoutId();
        entityManager.persist(user2);
        Item item1 = makeItem("Магнит", "сувенир", true, user1);
        entityManager.persist(item1);
        Item item2 = makeItem("Магний", "сувенир2", false, user1);
        entityManager.persist(item2);
        Item item3 = makeItem("магнитуда", "сувенир3", true, user2);
        entityManager.persist(item3);
        Item item4 = makeItem("Открытка", "сувенир4", true, user1);
        entityManager.persist(item4);
        Item item5 = makeItem("Сувенир", "магнит5", true, user2);
        entityManager.persist(item5);
        PageRequest pr = PageRequest.of(0, 20);
        List<Item> items = itemRepository.findByOwnerIdOrderById(user1.getId(), pr);
        assertThat(items).hasSize(3).contains(item1, item2, item4);
    }

    @Test
    @DisplayName("Получение списка предметов с Id запроса")
    void getItemsByRequestId() {
        User user1 = createUserWithoutId();
        entityManager.persist(user1);
        ItemRequest itemRequest = createRequestWithoutId();
        itemRequest.setRequester(user1);
        itemRequest.setDescription("Магни");
        entityManager.persist(itemRequest);
        User user2 = createUserTwoWithoutId();
        entityManager.persist(user2);
        Item item1 = makeItemWithRequestId("Магнит", "сувенир", user1,itemRequest.getId());
        entityManager.persist(item1);
        Item item2 = makeItem("Магний", "сувенир2", false, user1);
        entityManager.persist(item2);
        Item item3 = makeItemWithRequestId("магнитуда", "сувенир3", user2, itemRequest.getId());
        entityManager.persist(item3);
        Item item4 = makeItem("Открытка", "сувенир4", true, user1);
        entityManager.persist(item4);
        Item item5 = makeItemWithRequestId("Сувенир", "магнит5", user2, itemRequest.getId());
        entityManager.persist(item5);
        List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
        assertThat(items).hasSize(3).contains(item1, item3, item5);
    }

    private Item makeItem(String name, String description, Boolean available, User user) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(user);
        return item;
    }

    private Item makeItemWithRequestId(String name, String description, User user, long id) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequestId(id);
        return item;
    }
}