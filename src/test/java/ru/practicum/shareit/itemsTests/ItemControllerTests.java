package ru.practicum.shareit.itemsTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTests extends StorageForTests {

    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;

    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void findAllByUserId() throws Exception {
        ItemOwnerDto itemOwnerDto = createItemOwnerDto();
        when(itemService.getAllUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemOwnerDto));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemOwnerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemOwnerDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemOwnerDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemOwnerDto.getAvailable())))
                .andExpect(jsonPath("$[0].comments", hasSize(1)));
    }

    @Test
    void findItemByText() throws Exception {
        ItemDto itemDto = createItemDtoNullRequest();
        when(itemService.findItemsByText(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void addNewItem() throws Exception {
        ItemDto enterDto = createItemDtoNullRequestAndId();
        ItemDto exitDto = createItemDtoNullRequest();
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(exitDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(enterDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(exitDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(exitDto.getName())))
                .andExpect(jsonPath("$.description", is(exitDto.getDescription())))
                .andExpect(jsonPath("$.available", is(exitDto.getAvailable())));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto itemDto = createItemDtoNullRequest();
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void findById() throws Exception {
        ItemOwnerDto itemOwnerDto = createItemOwnerDto();
        when(itemService.findItemOwnerDtoById(anyLong(), anyLong()))
                .thenReturn(itemOwnerDto);

        mvc.perform(get("/items/{itemId}", itemOwnerDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemOwnerDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemOwnerDto.getName())))
                .andExpect(jsonPath("$.description", is(itemOwnerDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemOwnerDto.getAvailable())))
                .andExpect(jsonPath("$.comments", hasSize(1)));
    }

    @Test
    void deleteItem() throws Exception {
        ItemDto itemDto = createItemDtoNullRequest();
        doNothing().when(itemService).deleteItemById(anyLong(), anyLong());

        mvc.perform(delete("/items/{itemId}", itemDto.getId())
                        .header(HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        ItemDto itemDto = createItemDtoNullRequest();
        CommentDto commentDto = createCommentDto();
        when(commentService.addCommentForItem(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));
    }

    @Test
    void updateItemTestWithIncorrectId() throws Exception {
        ItemDto itemDto = createItemDtoNullRequest();
        Mockito.when(itemService.updateItem(anyLong(),anyLong(), any(ItemDto.class)))
                .thenThrow(ItemNotFoundException.class);
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}