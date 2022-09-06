package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository {
    ItemRequest addRequest(long userId, ItemRequest itemRequest);
}