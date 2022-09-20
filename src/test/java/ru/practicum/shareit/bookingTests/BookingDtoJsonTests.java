package ru.practicum.shareit.bookingTests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;

import java.io.IOException;

@JsonTest
public class BookingDtoJsonTests extends StorageForTests {
    @Autowired
    private JacksonTester<BookingDto> json;
    @Autowired
    private JacksonTester<BookingDtoOnlyId> json2;

    @Test
    void jsonBookingDto() throws IOException {
        BookingDto bookingDto = createBookingDto();
        bookingDto.setBooker(null);
        bookingDto.setItem(null);
        JsonContent<BookingDto> result = json.write(bookingDto);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) bookingDto.getId());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().toString());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().toString());
        Assertions.assertThat(result).extractingJsonPathValue("$.item")
                .isEqualTo(null);
        Assertions.assertThat(result).extractingJsonPathValue("$.booker")
                .isEqualTo(null);
        Assertions.assertThat(result).extractingJsonPathStringValue("status")
                .isEqualTo(bookingDto.getStatus().toString());
    }

    @Test
    void jsonBookingDtoOnlyId1() throws Exception {
        BookingDtoOnlyId bookingDtoOnlyId = createBookingDtoOnlyIdLast();
        JsonContent<BookingDtoOnlyId> result = json2.write(bookingDtoOnlyId);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) bookingDtoOnlyId.getId());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDtoOnlyId.getStart().toString());
        Assertions.assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDtoOnlyId.getEnd().toString());
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(Math.toIntExact(bookingDtoOnlyId.getItemId()));
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(Math.toIntExact(bookingDtoOnlyId.getBookerId()));
        Assertions.assertThat(result).extractingJsonPathStringValue("status")
                .isEqualTo(bookingDtoOnlyId.getStatus().toString());
    }
}