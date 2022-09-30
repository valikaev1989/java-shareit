package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ValidatorServer;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final ValidatorServer validator;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    /**
     * Получение списка своих запросов с ответами на них
     *
     * @param userId id пользователя
     */
    @Override
    public List<ItemRequestWithItemDto> getOwnRequests(long userId) {
        validator.validateAndReturnUserByUserId(userId);
        List<ItemRequest> requests = requestRepository.findAllByRequesterId(userId);
        return getListRequest(requests);
    }

    /**
     * Получение списка запросов, созданных другими пользователями
     *
     * @param userId id пользователя
     * @param from   индекс первого элемента
     * @param size   количество элементов для отображения
     */
    @Override
    public List<ItemRequestWithItemDto> getAllRequests(long userId, int from, int size) {
        validator.validateAndReturnUserByUserId(userId);
//        validator.validatePage(from, size);
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdIsNot(userId, pageable);
        return getListRequest(requests);
    }

    /**
     * Добавление нового запроса
     *
     * @param userId         id пользователя
     * @param itemRequestDto запрос
     */
    @Transactional
    @Override
    public ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = validator.validateAndReturnUserByUserId(userId);
//        validator.validateItemRequestDesc(itemRequestDto);
        return itemRequestMapper.toItemRequestDto(
                requestRepository.save(itemRequestMapper.toItemRequest(user, itemRequestDto)));
    }

    /**
     * Получение данных о конкретном запросе с ответами на него
     *
     * @param userId    id пользователя
     * @param requestId id запроса
     */
    @Override
    public ItemRequestWithItemDto getRequest(long userId, long requestId) {
        validator.validateAndReturnUserByUserId(userId);
        ItemRequest itemRequest = validator.validateAndReturnItemRequestByRequestId(requestId);
        return itemRequestMapper.toItemRequestWithItemDto(itemRequest, getListItemDtoByRequestId(requestId));
    }

    /**
     * Получение списка запрошенных вещей по идентификатору запроса
     *
     * @param requestId id запроса
     */
    private List<ItemDto> getListItemDtoByRequestId(long requestId) {
        return itemMapper.toItemDtoList(itemRepository.findByRequestId(requestId));
    }

    /**
     * Получение списка своих запросов со списком вещей подходящих по запросу
     *
     * @param requests список запросов вещей
     */
    private List<ItemRequestWithItemDto> getListRequest(List<ItemRequest> requests) {
        return requests.stream().map(itemRequest -> itemRequestMapper.toItemRequestWithItemDto(
                itemRequest, getListItemDtoByRequestId(itemRequest.getId()))).collect(Collectors.toList());
    }
}