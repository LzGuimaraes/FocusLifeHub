package dev.LzGuimaraes.FocusLifeHub.Contas;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder; 
import org.springframework.stereotype.Service;

import dev.LzGuimaraes.FocusLifeHub.Exceptions.ResourceNotFoundException;
import dev.LzGuimaraes.FocusLifeHub.Contas.dto.ContasRequestDTO;
import dev.LzGuimaraes.FocusLifeHub.Contas.dto.ContasResponseDTO;
import dev.LzGuimaraes.FocusLifeHub.Financas.FinancasModel;
import dev.LzGuimaraes.FocusLifeHub.Financas.FinancasRepository;
import dev.LzGuimaraes.FocusLifeHub.Security.JWTUserData;

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

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JWTUserData jwtData = (JWTUserData) authentication.getPrincipal();
        return jwtData.userId();
    }

    public Page<ContasResponseDTO> getAllContas(Pageable pageable) {
        Long userId = getAuthenticatedUserId();
        return contasRepository.findByFinancas_UserId(userId, pageable)
                .map(contasMapper::toResponse);
    }

    public ContasResponseDTO getContaById(Long id) {
        Long userId = getAuthenticatedUserId();
        ContasModel conta = contasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta com ID " + id + " não encontrada"));

        if (!conta.getFinancas().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Conta com ID " + id + " não encontrada");
        }

        return contasMapper.toResponse(conta);
    }

    public List<ContasResponseDTO> getContasByFinancaId(Long financasId) {
        Long userId = getAuthenticatedUserId();
        FinancasModel financa = financasRepository.findById(financasId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira (Financa) com ID " + financasId + " não encontrada"));

        if (!financa.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Carteira (Financa) com ID " + financasId + " não encontrada");
        }
        
        return contasRepository.findByFinancasId(financasId)
                .stream()
                .map(contasMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ContasResponseDTO createConta(ContasRequestDTO dto) {
        Long userId = getAuthenticatedUserId();

        FinancasModel financa = financasRepository.findById(dto.financas_id())
                .orElseThrow(() -> new ResourceNotFoundException("Carteira (Financa) com ID " + dto.financas_id() + " não encontrada"));

        if (!financa.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Carteira (Financa) com ID " + dto.financas_id() + " não encontrada");
        }
        
        ContasModel conta = contasMapper.toModel(dto, financa);

        float saldoInicial = dto.saldo() != null ? dto.saldo() : 0f;

        if ("Despesa".equalsIgnoreCase(dto.tipo())) {
            saldoInicial = -Math.abs(saldoInicial);
        } else {
            saldoInicial = Math.abs(saldoInicial);
        }

        conta.setSaldo(saldoInicial);
        ContasModel savedConta = contasRepository.save(conta);
        return contasMapper.toResponse(savedConta);
    }

    public ContasResponseDTO updateConta(Long id, ContasRequestDTO dto) {
        Long userId = getAuthenticatedUserId();
        ContasModel conta = contasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta com ID " + id + " não encontrada para alteração"));

        if (!conta.getFinancas().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Conta com ID " + id + " não encontrada para alteração");
        }

        if (dto.nome() != null && !dto.nome().isBlank()) {
            conta.setNome(dto.nome());
        }

        if (dto.tipo() != null && !dto.tipo().isBlank()) {
            conta.setTipo(dto.tipo());
        }

        if (dto.financas_id() != null && !dto.financas_id().equals(conta.getFinancas().getId())) {
            FinancasModel newFinanca = financasRepository.findById(dto.financas_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Nova carteira (Financa) com ID " + dto.financas_id() + " não encontrada"));
            
            if (!newFinanca.getUser().getId().equals(userId)) {
                 throw new ResourceNotFoundException("Nova carteira (Financa) com ID " + dto.financas_id() + " não encontrada");
            }
            conta.setFinancas(newFinanca);
        }

        if (dto.saldo() != null) {
            float novoSaldo = dto.saldo();
            String tipoConta = (dto.tipo() != null && !dto.tipo().isBlank()) ? dto.tipo() : conta.getTipo();

            if ("Despesa".equalsIgnoreCase(tipoConta)) {
                novoSaldo = -Math.abs(novoSaldo);
            } else {
                novoSaldo = Math.abs(novoSaldo);
            }
            conta.setSaldo(novoSaldo);
        }

        ContasModel updatedConta = contasRepository.save(conta);
        return contasMapper.toResponse(updatedConta);
    }

    public void deleteConta(Long id) {
        Long userId = getAuthenticatedUserId();
        
        ContasModel conta = contasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta com ID " + id + " não encontrada para exclusão"));
        
        if (!conta.getFinancas().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Conta com ID " + id + " não encontrada para exclusão");
        }

        contasRepository.deleteById(id);
    }
}