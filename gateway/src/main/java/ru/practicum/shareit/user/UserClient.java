package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.util.ValidatorGateway;

@Service
public class UserClient extends BaseClient {
    private final ValidatorGateway validator;

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder,
                      ValidatorGateway validator) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
        this.validator = validator;
    }

    public ResponseEntity<Object> addNewUser(UserDto userDto) {
        validator.validateUserDTO(userDto);
        return post("", userDto);
    }

    public ResponseEntity<Object> updateUser(UserDto userDto, long userId) {
        validator.validateId(userId);
        validator.validateEmailUser(userDto);
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> getUsers() {
        return get("");
    }

    public void deleteUser(long userId) {
        validator.validateId(userId);
        delete("/" + userId);
    }

    public ResponseEntity<Object> findById(long userId) {
        validator.validateId(userId);
        return get("/" + userId);
    }
}