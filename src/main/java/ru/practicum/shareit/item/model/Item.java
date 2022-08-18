package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class Item {
    private Long id;
    @NotBlank
    private String name;
    private String description;

    @NonNull
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
