package ru.practicum.shareit.itemRequestsTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests extends StorageForTests {
    private static final String HEADER = "X-Sharer-User-Id";
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("ControllerMVC Тест получения всех запросов")
    void getAllRequests() throws Exception {
        ItemRequestWithItemDto expectedRequest = createRequestWithItemDto();
        expectedRequest.setItems(List.of(createItemDtoWithRequestId()));
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(expectedRequest));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expectedRequest.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(expectedRequest.getDescription())))
                .andExpect(jsonPath("$[0].created", is(expectedRequest.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }

    @Test
    @DisplayName("ControllerMVC Тест добавления запроса на предмет")
    void addNewItemRequest() throws Exception {
        ItemRequestDto itemRequestDto = createRequestDtoNullId();
        ItemRequestDto expectedRequest = createRequestDto();
        when(itemRequestService.addItemRequest(anyLong(), any()))
                .thenReturn(expectedRequest);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(expectedRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(expectedRequest.getCreated().toString())))
                .andExpect(jsonPath("$.requesterId", is(expectedRequest.getRequesterId()), Long.class));
    }

    @Test
    @DisplayName("ControllerMVC Тест получения запросов пользователя на предмет")
    void getOwnRequests() throws Exception {
        ItemRequestWithItemDto expectedRequest = createRequestWithItemDto();
        expectedRequest.setItems(List.of(createItemDtoWithRequestId()));
        when(itemRequestService.getOwnRequests(anyLong()))
                .thenReturn(List.of(expectedRequest));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expectedRequest.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(expectedRequest.getDescription())))
                .andExpect(jsonPath("$[0].created", is(expectedRequest.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }

    @Test
    @DisplayName("ControllerMVC Тест получения запроса на предмет")
    void getRequest() throws Exception {
        ItemRequestWithItemDto expectedRequest = createRequestWithItemDto();
        when(itemRequestService.getRequest(anyLong(), anyLong()))
                .thenReturn(expectedRequest);
        mvc.perform(get("/requests/{requestId}", expectedRequest.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(expectedRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(expectedRequest.getCreated().toString())))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }
}