package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.requestDto.RequestDto;
import ru.practicum.shareit.util.ValidatorGateway;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";
    private final ValidatorGateway validator;

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder,
                         ValidatorGateway validator) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
        this.validator = validator;
    }

    public ResponseEntity<Object> addRequest(long userId, RequestDto requestDto) {
        validator.validateId(userId);
        validator.validateItemRequestDesc(requestDto);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getOwnRequests(long userId) {
        validator.validateId(userId);
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(long userId, int[] page) {
        validator.validateId(userId);
        validator.validatePage(page[0], page[1]);
        Map<String, Object> parameters = Map.of(
                "from", page[0],
                "size", page[1]
        );
        return get("/all/?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequest(long userId, long requestId) {
        validator.validateId(userId);
        validator.validateId(requestId);
        return get("/" + requestId, userId);
    }
}