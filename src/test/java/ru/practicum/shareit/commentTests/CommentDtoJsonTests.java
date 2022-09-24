package ru.practicum.shareit.commentTests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.comment.dto.CommentDto;

@JsonTest
public class CommentDtoJsonTests extends StorageForTests {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    @DisplayName("Тест json CommentDto")
    void jsonCommentDto() throws Exception {
        CommentDto commentDto = createCommentDto();
        String created = commentDto.getCreated().toString();
        JsonContent<CommentDto> result = json.write(commentDto);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("comment");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo("NameDtoTwo");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(created);
    }
}