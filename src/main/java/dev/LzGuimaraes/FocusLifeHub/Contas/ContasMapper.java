package dev.LzGuimaraes.FocusLifeHub.Contas;

import org.springframework.stereotype.Component;

import dev.LzGuimaraes.FocusLifeHub.Contas.dto.ContasRequestDTO;
import dev.LzGuimaraes.FocusLifeHub.Contas.dto.ContasResponseDTO;
import dev.LzGuimaraes.FocusLifeHub.Financas.FinancasModel;

@Component
public class ContasMapper {

    public ContasModel toModel(ContasRequestDTO dto, FinancasModel financas) {
        if (dto == null) {
            return null;
        }

        ContasModel conta = new ContasModel();
        conta.setNome(dto.nome());
        conta.setTipo(dto.tipo());
        conta.setFinancas(financas);
        
        return conta;
    }

    public ContasResponseDTO toResponse(ContasModel conta) {
        if (conta == null) {
            return null;
        }

        Long financasId = (conta.getFinancas() != null) ? conta.getFinancas().getId() : null;

        return new ContasResponseDTO(
            conta.getId(),
            conta.getNome(),
            conta.getTipo(),
            conta.getSaldo(),
            financasId
        );
    }
}