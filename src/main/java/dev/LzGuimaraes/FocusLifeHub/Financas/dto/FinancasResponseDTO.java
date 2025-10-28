package dev.LzGuimaraes.FocusLifeHub.Financas.dto;

public record FinancasResponseDTO(
    Long id,
    String nome,
    String moeda,
    Long user_id
) {}