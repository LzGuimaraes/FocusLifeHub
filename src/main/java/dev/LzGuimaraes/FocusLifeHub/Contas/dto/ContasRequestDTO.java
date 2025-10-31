package dev.LzGuimaraes.FocusLifeHub.Contas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContasRequestDTO(

    @NotBlank(message = "O nome da conta é obrigatório (ex: 'Conta Corrente')")
    String nome,

    @NotBlank(message = "O tipo da conta é obrigatório (ex: 'Despesa', 'Saldo')")
    String tipo,

    @NotNull(message = "O ID da carteira (financas_id) é obrigatório")
    Long financas_id,

    Float saldo 
) {}