package ru.practicum.shareit.bookingTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.StorageForTests;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.ItemController;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests extends StorageForTests {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;
    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void addBooking() throws Exception {
        BookingDtoOnlyId bookingDtoOnlyId = createBookingDtoOnlyId();
        BookingDto bookingDto = createBookingDto();
        when(bookingService.addBooking(anyLong(), any(BookingDtoOnlyId.class)))
                .thenReturn(bookingDto);
        System.out.println(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoOnlyId))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void updateStatusBooking() throws Exception {
        BookingDto bookingDto = createBookingDto();
        BookingDto expectedBookingDto = createBookingDto();
        expectedBookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateStatusBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(expectedBookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .queryParam("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(expectedBookingDto.getStatus().toString())));
    }

    @Test
    void getBookingById() throws Exception {
        BookingDto bookingDto = createBookingDto();
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBookingsByBookerId() throws Exception {
        BookingDto bookingDto = createBookingDto();
        when(bookingService.getBookingsByBookerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBookingsByOwnerId() throws Exception {
        BookingDto bookingDto = createBookingDto();
        when(bookingService.getBookingsByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

}