package dev.LzGuimaraes.FocusLifeHub.User.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateDTO(
    @NotBlank(message = "O nome é obrigatório")
    String nome,

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    String email
) {}