package ru.practicum.shareit.requestsTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTests extends StorageForTests {
    private ItemRequestServiceImpl mockItemRequestService;
    @Mock
    private ItemRequestRepository mockRequestRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private Validator mockValidator;
    @Autowired
    private ItemRequestMapper itemRequestMapper;
    @Autowired
    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        mockItemRequestService = new ItemRequestServiceImpl
                (mockRequestRepository, mockItemRepository, mockValidator, itemRequestMapper, itemMapper);
    }

    @Test
    void getOwnRequests() {
        User user = createUser();
        Item item = createItemWithRequest();
        ItemRequest itemRequest = createRequest();
        itemRequest.setRequester(user);
        ItemDto itemDto = createItemDtoWithRequestId();
        ItemRequestWithItemDto itemRequestWithItemDto = createRequestWithItemDto();
        itemRequestWithItemDto.setItems(List.of(itemDto));
        List<ItemRequestWithItemDto> expectedRequest = List.of(itemRequestWithItemDto);
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockRequestRepository.findAllByRequesterId(anyLong())).thenReturn(List.of(itemRequest));
        when(mockItemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        List<ItemRequestWithItemDto> actualRequest = mockItemRequestService.getOwnRequests(user.getId());
        assertEquals(expectedRequest, actualRequest);
        assertEquals(itemDto, actualRequest.get(0).getItems().get(0));
    }

    @Test
    void getAllRequests() {
        User user = createUser();
        ItemRequest itemRequest = createRequest();
        Item item = createItemWithRequest();
        ItemDto expectedItemDto = createItemDtoWithRequestId();
        ItemRequestWithItemDto itemRequestWithItemDto = createRequestWithItemDto();
        itemRequestWithItemDto.setItems(List.of(expectedItemDto));
        List<ItemRequestWithItemDto> expectedRequest = List.of(itemRequestWithItemDto);
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockRequestRepository.findAllByRequesterIdIsNot(anyLong(),
                any(Pageable.class))).thenReturn(List.of(itemRequest));
        when(mockItemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        List<ItemRequestWithItemDto> actualRequest = mockItemRequestService
                .getAllRequests(user.getId(), 0, 5);
        assertEquals(expectedRequest, actualRequest);
        assertEquals(expectedItemDto, actualRequest.get(0).getItems().get(0));
    }

    @Test
    void addItemRequest() {
        User user = createUser();
        ItemRequest itemRequest = createRequest();
        ItemRequestDto expectedItemRequestDto = createRequestDto();
        itemRequest.setRequester(user);
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        ItemRequestDto actualItemRequestDto = mockItemRequestService
                .addItemRequest(user.getId(), expectedItemRequestDto);
        expectedItemRequestDto.setRequesterId(user.getId());
        assertEquals(expectedItemRequestDto, actualItemRequestDto);
    }

    @Test
    void getRequest() {
        User user = createUser();
        ItemRequest itemRequest = createRequest();
        Item item = createItemWithRequest();
        ItemDto itemDto = createItemDtoWithRequestId();
        ItemRequestWithItemDto expectedRequest = createRequestWithItemDto();
        expectedRequest.setItems(List.of(itemDto));
        when(mockValidator.validateAndReturnUserByUserId(anyLong())).thenReturn(user);
        when(mockValidator.validateAndReturnItemRequestByRequestId(anyLong())).thenReturn(itemRequest);
        when(mockItemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        ItemRequestWithItemDto actualRequest = mockItemRequestService
                .getRequest(user.getId(), itemRequest.getId());
        assertEquals(expectedRequest, actualRequest);
        assertEquals(itemDto, actualRequest.getItems().get(0));
    }
}