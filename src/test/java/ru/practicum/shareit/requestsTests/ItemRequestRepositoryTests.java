package ru.practicum.shareit.requestsTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestRepositoryTests extends StorageForTests {
    private final TestEntityManager entityManager;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Test
    void findAllByRequesterId() {
        User user = createUserWithoutId();
        entityManager.persist(user);
        ItemRequest itemRequest = createRequestWithoutId();
        itemRequest.setRequester(user);
        entityManager.persist(itemRequest);
        List<ItemRequest> expectedResult = List.of(itemRequest);
        List<ItemRequest> actualResult = itemRequestRepository.findAllByRequesterId(itemRequest.getRequester().getId());
        List<ItemRequest> q = itemRequestRepository.findAll();
        assertEquals(expectedResult, actualResult);
        assertEquals(itemRequest,actualResult.get(0));
    }

    @Test
    void findAllByRequesterIdIsNot() {
        User user = createUserWithoutId();
        entityManager.persist(user);
        ItemRequest itemRequest = createRequestWithoutId();
        itemRequest.setRequester(user);
        entityManager.persist(itemRequest);
        PageRequest pr = PageRequest.of(0, 20);
        User user2 = createUserTwoWithoutId();
        entityManager.persist(user2);
        List<ItemRequest> expectedResult = List.of(itemRequest);
        List<ItemRequest> actualResult = itemRequestRepository.findAllByRequesterIdIsNot(user2.getId(), pr);
        assertEquals(expectedResult, actualResult);
        assertEquals(itemRequest, actualResult.get(0));
    }
}