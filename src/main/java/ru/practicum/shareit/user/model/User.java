package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    @NotBlank
    private String name;
    @Email
    @NotNull
    private String email;
}