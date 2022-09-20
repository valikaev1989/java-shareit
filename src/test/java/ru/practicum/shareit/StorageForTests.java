package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOnlyId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class StorageForTests {
    private final LocalDateTime dateTime = LocalDateTime.parse("2022-11-11T10:10:10");
    private final LocalDateTime dateTimeLast = LocalDateTime.parse("2022-11-10T10:10:10");
    private final LocalDateTime dateTimeNext = LocalDateTime.parse("2022-11-12T10:10:10");

    protected UserDto createUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1);
        dto.setName("NameDto");
        dto.setEmail("email@email.ru");
        return dto;
    }

    protected UserDto createUserDtoWithoutId() {
        UserDto dto = new UserDto();
        dto.setName("NameDto");
        dto.setEmail("email@email.ru");
        return dto;
    }

    protected UserDto createUserDtoTwo() {
        UserDto dto = new UserDto();
        dto.setId(2);
        dto.setName("NameDtoTwo");
        dto.setEmail("emailTwo@email.ru");
        return dto;
    }

    protected UserDto createUserDtoTwoWithoutId() {
        UserDto dto = new UserDto();
        dto.setName("NameDtoTwo");
        dto.setEmail("emailTwo@email.ru");
        return dto;
    }

    protected UserDto createUserDtoThreeWithoutId() {
        UserDto dto = new UserDto();
        dto.setName("NameDtoThree");
        dto.setEmail("emailThree@email.ru");
        return dto;
    }

    protected User createUser() {
        User user = new User();
        user.setId(1);
        user.setName("NameDto");
        user.setEmail("email@email.ru");
        return user;
    }

    protected User createUserWithoutId() {
        User user = new User();
        user.setName("NameDto");
        user.setEmail("email@email.ru");
        return user;
    }

    protected User createUserTwo() {
        User user = new User();
        user.setId(2);
        user.setName("NameDtoTwo");
        user.setEmail("emailTwo@email.ru");
        return user;
    }

    protected User createUserTwoWithoutId() {
        User user = new User();
        user.setName("NameDtoTwo");
        user.setEmail("emailTwo@email.ru");
        return user;
    }

    protected User createUserThreeWithoutId() {
        User user = new User();
        user.setName("NameDtoThree");
        user.setEmail("emailThree@email.ru");
        return user;
    }

    protected ItemRequest createRequest() {
        ItemRequest request = new ItemRequest();
        request.setId(1);
        request.setDescription("DescriptionRequest");
        request.setRequester(createUserTwo());
        request.setCreated(dateTime);
        return request;
    }

    protected ItemRequest createRequestWithoutId() {
        ItemRequest request = new ItemRequest();
        request.setDescription("DescriptionRequest");
        request.setRequester(createUser());
        request.setCreated(dateTime);
        return request;
    }

    protected ItemRequestDto createRequestDto() {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(1);
        request.setDescription("DescriptionRequest");
        request.setRequesterId(createUserTwo().getId());
        request.setCreated(dateTime);
        return request;
    }

    protected ItemRequestDto createRequestDtoNullId() {
        ItemRequestDto request = new ItemRequestDto();
        request.setDescription("DescriptionRequest");
        request.setRequesterId(createUserTwo().getId());
        request.setCreated(dateTime);
        return request;
    }

    protected ItemRequestWithItemDto createRequestWithItemDto() {
        ItemRequestWithItemDto request = new ItemRequestWithItemDto();
        request.setId(1);
        request.setDescription("DescriptionRequest");
        request.setCreated(dateTime);
        request.setItems(List.of());
        return request;
    }

    protected Item createItemNullRequest() {
        Item item = new Item();
        item.setId(1);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(createUser());
        return item;
    }

    protected Item createItemNullRequest2() {
        Item item = new Item();
        item.setId(2);
        item.setName("NameItem2");
        item.setDescription("DescriptionItem2");
        item.setAvailable(true);
        item.setOwner(createUser());
        return item;
    }

    protected Item createItemNullRequest3() {
        Item item = new Item();
        item.setId(3);
        item.setName("NameItem3");
        item.setDescription("DescriptionItem3");
        item.setAvailable(true);
        item.setOwner(createUser());
        return item;
    }

    protected Item createItemWithoutId(User user) {
        Item item = new Item();
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(user);
        return item;
    }

    protected Item createItemWithoutId2(User user) {
        Item item = new Item();
        item.setName("NameItem2");
        item.setDescription("DescriptionItem2");
        item.setAvailable(true);
        item.setOwner(user);
        return item;
    }

    protected Item createItemWithoutId3(User user) {
        Item item = new Item();
        item.setName("NameItem3");
        item.setDescription("DescriptionItem3");
        item.setAvailable(true);
        item.setOwner(user);
        return item;
    }

    protected Item createItemWithRequest() {
        Item item = new Item();
        item.setId(1);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(createUser());
        item.setRequestId(createRequest().getId());
        return item;
    }


    protected ItemDto createItemDtoNullRequest() {
        ItemDto item = new ItemDto();
        item.setId(1);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwnerId(createUserDto().getId());
        return item;
    }

    protected ItemDto createItemDtoNullRequestAndId() {
        ItemDto item = new ItemDto();
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwnerId(createUserDto().getId());
        return item;
    }

    protected ItemDto createItemDtoNullRequestAndIdTwo() {
        ItemDto item = new ItemDto();
        item.setName("NameItemTwo");
        item.setDescription("DescriptionItemTwo");
        item.setAvailable(true);
        item.setOwnerId(createUserTwo().getId());
        return item;
    }

    protected ItemDto createItemDtoNullRequest2() {
        ItemDto item = new ItemDto();
        item.setId(2);
        item.setName("NameItem2");
        item.setDescription("DescriptionItem2");
        item.setAvailable(true);
        item.setOwnerId(createUserDto().getId());
        return item;
    }

    protected ItemDto createItemDtoWithRequestIdNullOwner() {
        ItemDto item = new ItemDto();
        item.setId(1);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setRequestId(1L);
        return item;
    }

    protected ItemDto createItemDtoWithRequestId() {
        ItemDto item = new ItemDto();
        item.setId(1);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwnerId(1L);
        item.setRequestId(1L);
        return item;
    }

    protected ItemOwnerDto createItemOwnerDto() {
        ItemOwnerDto dtoBooking = new ItemOwnerDto();
        dtoBooking.setId(1);
        dtoBooking.setName("NameItem");
        dtoBooking.setDescription("DescriptionItem");
        dtoBooking.setAvailable(true);
        dtoBooking.setLastBooking(createBookingDtoOnlyIdLast());
        dtoBooking.setNextBooking(createBookingDtoOnlyIdNext());
        dtoBooking.setComments(List.of(createCommentDto2()));
        return dtoBooking;
    }

    protected ItemOwnerDto createItemUserDto() {
        ItemOwnerDto dtoBooking = new ItemOwnerDto();
        dtoBooking.setId(1);
        dtoBooking.setName("NameItem");
        dtoBooking.setDescription("DescriptionItem");
        dtoBooking.setAvailable(true);
        dtoBooking.setLastBooking(null);
        dtoBooking.setNextBooking(null);
        dtoBooking.setComments(List.of(createCommentDto2()));
        return dtoBooking;
    }

    protected Booking createBookingWithoutId(User user, Item item) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(dateTimeLast);
        booking.setEnd(dateTimeNext);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    protected Booking createBooking(User user, Item item, Long bookerId) {
        Booking booking = new Booking();
        booking.setId(bookerId);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(dateTimeLast);
        booking.setEnd(dateTimeNext);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    protected Booking createBooking() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setItem(createItemNullRequest());
        booking.setBooker(createUser());
        booking.setStart(dateTimeLast.minusDays(1));
        booking.setEnd(dateTimeNext.minusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }


    protected Booking createBooking2() {
        Booking booking = new Booking();
        booking.setId(2);
        booking.setItem(createItemNullRequest());
        booking.setBooker(createUserTwo());
        booking.setStart(dateTimeLast.minusDays(1));
        booking.setEnd(dateTimeNext.minusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    protected Booking createLastBooking() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setItem(createItemNullRequest());
        booking.setBooker(createUserTwo());
        booking.setStart(dateTimeLast.minusDays(1));
        booking.setEnd(dateTimeNext.minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }

    protected Booking createNextBooking() {
        Booking booking = new Booking();
        booking.setId(2);
        booking.setItem(createItemNullRequest());
        booking.setBooker(createUserTwo());
        booking.setStart(dateTimeLast.plusDays(3));
        booking.setEnd(dateTimeNext.plusDays(3));
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    protected BookingDto createBookingDto() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1);
        bookingDto.setItem(createItemDtoNullRequest());
        bookingDto.setBooker(createUserDto());
        bookingDto.setStart(dateTimeLast.minusDays(1));
        bookingDto.setEnd(dateTimeNext.minusDays(1));
        bookingDto.setStatus(BookingStatus.WAITING);
        return bookingDto;
    }

    protected BookingDto createBookingDto2() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(2);
        bookingDto.setItem(createItemDtoNullRequest());
        bookingDto.setBooker(createUserDtoTwo());
        bookingDto.setStart(dateTimeLast.minusDays(1));
        bookingDto.setEnd(dateTimeNext.minusDays(1));
        bookingDto.setStatus(BookingStatus.WAITING);
        return bookingDto;
    }

    protected BookingDtoOnlyId createBookingDtoOnlyIdLast() {
        BookingDtoOnlyId booking = new BookingDtoOnlyId();
        booking.setId(1);
        booking.setItemId(1L);
        booking.setBookerId(1L);
        booking.setStart(dateTimeLast.minusDays(1));
        booking.setEnd(dateTimeNext.minusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    protected BookingDtoOnlyId createBookingDtoOnlyId() {
        BookingDtoOnlyId booking = new BookingDtoOnlyId();
        booking.setId(1);
        booking.setItemId(1L);
        booking.setStart(dateTimeLast.minusDays(1));
        booking.setEnd(dateTimeNext.minusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    protected BookingDtoOnlyId createBookingDtoOnlyIdNext() {
        BookingDtoOnlyId booking = new BookingDtoOnlyId();
        booking.setId(2);
        booking.setItemId(1L);
        booking.setBookerId(2L);
        booking.setStart(dateTimeLast);
        booking.setEnd(dateTimeNext);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    protected BookingDtoOnlyId createBookingForComment() {
        BookingDtoOnlyId booking = new BookingDtoOnlyId();
        booking.setItemId(1L);
        booking.setStart(LocalDateTime.now().plusSeconds(1).minusNanos(100));
        booking.setEnd(LocalDateTime.now().plusSeconds(1));
        return booking;
    }

    protected Comment createComment() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setAuthor(createUserTwo());
        comment.setText("comment");
        comment.setItem(createItemNullRequest());
        comment.setCreated(dateTime);
        return comment;
    }

    protected Comment createCommentWithoutId(User user, Item item) {
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setText("comment");
        comment.setItem(item);
        comment.setCreated(dateTime);
        return comment;
    }

    protected CommentDto createCommentDto() {
        CommentDto comment = new CommentDto();
        comment.setId(1);
        comment.setAuthorName("NameDtoTwo");
        comment.setText("comment");
        comment.setCreated(dateTime.plusDays(3));
        return comment;
    }

    protected CommentDto createCommentDto2() {
        CommentDto comment = new CommentDto();
        comment.setId(1);
        comment.setAuthorName("NameDtoTwo");
        comment.setText("comment");
        comment.setCreated(dateTime);
        return comment;
    }

    protected CommentDto createCommentDtoWithoutId() {
        CommentDto comment = new CommentDto();
        comment.setAuthorName("NameDtoTwo");
        comment.setText("comment");
        comment.setCreated(dateTime);
        return comment;
    }
}