package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.commentDto.CommentDto;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.util.ValidatorGateway;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";
    private final ValidatorGateway validator;


    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder,
                      ValidatorGateway validator) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
        this.validator = validator;
    }

    public ResponseEntity<Object> addNewItem(long userId, ItemDto itemDto) {
        validator.validateId(userId);
        validator.validateItemAll(itemDto);
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> findItemById(long userId, long itemId) {
        validator.validateId(userId);
        validator.validateId(itemId);
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto itemDto) {
        validator.validateId(userId);
        validator.validateId(itemId);
        validator.validateForUpdateItem(itemDto);
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> findItemByText(String text, long userId, int[] page) {
        validator.validatePage(page[0], page[1]);
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", page[0],
                "size", page[1]
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public void deleteItem(long userId, long itemId) {
        validator.validateId(userId);
        validator.validateId(itemId);
        delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> findAllByUserId(long userId, int[] page) {
        validator.validateId(userId);
        validator.validatePage(page[0], page[1]);
        Map<String, Object> parameters = Map.of(
                "from", page[0],
                "size", page[1]
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentDto commentDto) {
        validator.validateId(userId);
        validator.validateId(itemId);
        validator.validateCommentText(commentDto);
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}