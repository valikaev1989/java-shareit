package ru.practicum.shareit.bookingTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingMapperTests extends StorageForTests {
    private final BookingMapper bookingMapper;

    @Test
    @DisplayName("Тест маппинга в Booking")
    void toBooking() {
        Booking expectedBooking = createBooking();
        Booking actualBooking = bookingMapper
                .newBooking(createBookingDtoOnlyIdLast(), createUser(),
                        createItemNullRequest(), BookingStatus.WAITING);
        actualBooking.setId(1);
        assertEquals(expectedBooking.toString(), actualBooking.toString());
    }

    @Test
    @DisplayName("Тест маппинга в BookingDto")
    void toBookingDto() {
        BookingDto expectedBookingDto = createBookingDto();
        BookingDto actualBookingDto = bookingMapper.toBookingDto(createBooking());
        assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    @DisplayName("Тест маппинга в BookingDtoOnlyId")
    void toBookingDtoOnlyId() {
        BookingDtoOnlyId expectedBooking = createBookingDtoOnlyIdLast();
        BookingDtoOnlyId actualBooking = bookingMapper.toBookingDtoOnlyId(createBooking());
        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    @DisplayName("Тест маппинга в список с BookingDto")
    void toBookingDtoList() {
        List<BookingDto> expectedBookingDtoList = List.of(createBookingDto());
        List<BookingDto> actualBookingDtoList = bookingMapper.toBookingDtoList(List.of(createBooking()));
        assertEquals(expectedBookingDtoList, actualBookingDtoList);
        assertEquals(createBookingDto(), actualBookingDtoList.get(0));
    }
}