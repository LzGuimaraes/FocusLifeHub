package dev.LzGuimaraes.FocusLifeHub.User.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequest(
    @NotEmpty(message = "Name cannot be empty")
    String name,
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Formato de e-mail inválido")
    String email,
    @NotEmpty(message = "Senha cannot be empty")
    String password
) {}