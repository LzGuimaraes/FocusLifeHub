package dev.LzGuimaraes.FocusLifeHub.Financas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FinancasRequestDTO(

    @NotBlank(message = "O nome é obrigatório (ex: 'Carteira', 'Conta Corrente')")
    String nome,

    @NotBlank(message = "A moeda é obrigatória (ex: 'BRL', 'USD')")
    @Size(min = 3, max = 3, message = "A moeda deve ter 3 caracteres (ex: 'BRL')")
    String moeda
) {}