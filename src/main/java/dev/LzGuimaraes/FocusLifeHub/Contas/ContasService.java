package dev.LzGuimaraes.FocusLifeHub.Contas;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.LzGuimaraes.FocusLifeHub.Exceptions.ResourceNotFoundException;
import dev.LzGuimaraes.FocusLifeHub.Contas.dto.ContasRequestDTO;
import dev.LzGuimaraes.FocusLifeHub.Contas.dto.ContasResponseDTO;
import dev.LzGuimaraes.FocusLifeHub.Financas.FinancasModel;
import dev.LzGuimaraes.FocusLifeHub.Financas.FinancasRepository; 

@Service
public class ContasService {

    private final ContasRepository contasRepository;
    private final FinancasRepository financasRepository; 
    private final ContasMapper contasMapper;

    public ContasService(
            ContasRepository contasRepository,
            FinancasRepository financasRepository,
            ContasMapper contasMapper) {
        this.contasRepository = contasRepository;
        this.financasRepository = financasRepository;
        this.contasMapper = contasMapper;
    }

    public Page<ContasResponseDTO> getAllContas(Pageable pageable) {
        return contasRepository.findAll(pageable)
                .map(contasMapper::toResponse);
    }

    public ContasResponseDTO getContaById(Long id) {
        return contasRepository.findById(id)
                .map(contasMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Conta com ID " + id + " não encontrada"));
    }

    public List<ContasResponseDTO> getContasByFinancaId(Long financasId) {
        if (!financasRepository.existsById(financasId)) {
            throw new ResourceNotFoundException("Carteira (Financa) com ID " + financasId + " não encontrada");
        }
        
        return contasRepository.findByFinancasId(financasId)
                .stream()
                .map(contasMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ContasResponseDTO createConta(ContasRequestDTO dto) {
        FinancasModel financa = financasRepository.findById(dto.financas_id())
                .orElseThrow(() -> new ResourceNotFoundException("Carteira (Financa) com ID " + dto.financas_id() + " não encontrada"));

        ContasModel conta = contasMapper.toModel(dto, financa);
        conta.setSaldo(0.0f);

        ContasModel savedConta = contasRepository.save(conta);

        return contasMapper.toResponse(savedConta);
    }

    public ContasResponseDTO updateConta(Long id, ContasRequestDTO dto) {
        ContasModel conta = contasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta com ID " + id + " não encontrada para alteração"));

        if (dto.nome() != null && !dto.nome().isBlank()) {
            conta.setNome(dto.nome());
        }
        if (dto.tipo() != null && !dto.tipo().isBlank()) {
            conta.setTipo(dto.tipo());
        }

        if (dto.financas_id() != null && !dto.financas_id().equals(conta.getFinancas().getId())) {
            FinancasModel newFinanca = financasRepository.findById(dto.financas_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Nova carteira (Financa) com ID " + dto.financas_id() + " não encontrada"));
            conta.setFinancas(newFinanca);
        }


        ContasModel updatedConta = contasRepository.save(conta);

        return contasMapper.toResponse(updatedConta);
    }

    public void deleteConta(Long id) {
        if (!contasRepository.existsById(id)) {
            throw new ResourceNotFoundException("Conta com ID " + id + " não encontrada para exclusão");
        }
        contasRepository.deleteById(id);
    }
}