package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;

import java.util.List;

public interface ItemRequestService {

    /**
     * Получение списка своих запросов с ответами на них
     */
    List<ItemRequestWithItemDto> getOwnRequests(long userId);

    /**
     * Получение списка запросов, созданных другими пользователями
     */
    List<ItemRequestWithItemDto> getAllRequests(long userId, int from, int size);

    /**
     * Добавление нового запроса
     */
    ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto);

    /**
     * Получение данных о конкретном запросе с ответами на него
     */
    ItemRequestWithItemDto getRequest(long userId, long requestId);
}