package dev.LzGuimaraes.FocusLifeHub.Contas.dto;

public record ContasResponseDTO(
    Long id,
    String nome,
    String tipo,
    Float saldo,
    Long financas_id
) {}