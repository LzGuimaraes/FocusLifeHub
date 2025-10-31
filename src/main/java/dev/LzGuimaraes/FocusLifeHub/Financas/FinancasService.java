package dev.LzGuimaraes.FocusLifeHub.Financas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder; 
import org.springframework.stereotype.Service;

import dev.LzGuimaraes.FocusLifeHub.Exceptions.ResourceNotFoundException;
import dev.LzGuimaraes.FocusLifeHub.Financas.dto.FinancasRequestDTO;
import dev.LzGuimaraes.FocusLifeHub.Financas.dto.FinancasResponseDTO;
import dev.LzGuimaraes.FocusLifeHub.config.JWTUserData;
import dev.LzGuimaraes.FocusLifeHub.User.UserModel;
import dev.LzGuimaraes.FocusLifeHub.User.UserRepository;

@Service
public class FinancasService {

    private final FinancasRepository financasRepository;
    private final UserRepository userRepository;
    private final FinancasMapper financasMapper;

    public FinancasService(FinancasRepository financasRepository, UserRepository userRepository, FinancasMapper financasMapper) {
        this.financasRepository = financasRepository;
        this.userRepository = userRepository;
        this.financasMapper = financasMapper;
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JWTUserData jwtData = (JWTUserData) authentication.getPrincipal();
        return jwtData.userId();
    }

    public Page<FinancasResponseDTO> getAllFinancas(Pageable pageable) {
        Long userId = getAuthenticatedUserId();
        return financasRepository.findByUserId(userId, pageable)
                .map(financasMapper::toResponse);
    }

    public FinancasResponseDTO getFinancaById(Long id) {
        Long userId = getAuthenticatedUserId();
        FinancasModel financa = financasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta financeira com ID " + id + " não encontrada"));

        if (!financa.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Conta financeira com ID " + id + " não encontrada");
        }

        return financasMapper.toResponse(financa);
    }

    public FinancasResponseDTO createFinanca(FinancasRequestDTO dto) {
        Long authenticatedUserId = getAuthenticatedUserId();
        UserModel user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + authenticatedUserId + " não encontrado"));

        FinancasModel financa = financasMapper.toModel(dto, user);
        FinancasModel savedFinanca = financasRepository.save(financa);

        return financasMapper.toResponse(savedFinanca);
    }

    public FinancasResponseDTO updateFinanca(Long id, FinancasRequestDTO dto) {
        Long userId = getAuthenticatedUserId();
        FinancasModel financa = financasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta financeira com ID " + id + " não encontrada para alteração"));

        if (!financa.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Conta financeira com ID " + id + " não encontrada para alteração");
        }

        if (dto.nome() != null && !dto.nome().isBlank()) {
            financa.setNome(dto.nome());
        }
        if (dto.moeda() != null && !dto.moeda().isBlank()) {
            financa.setMoeda(dto.moeda().toUpperCase());
        }

        FinancasModel updatedFinanca = financasRepository.save(financa);

        return financasMapper.toResponse(updatedFinanca);
    }

    public void deleteFinanca(Long id) {
        Long userId = getAuthenticatedUserId();

        FinancasModel financa = financasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta financeira com ID " + id + " não encontrada para exclusão"));

        if (!financa.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Conta financeira com ID " + id + " não encontrada para exclusão");
        }

        financasRepository.deleteById(id);
    }
}