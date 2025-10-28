package dev.LzGuimaraes.FocusLifeHub.Financas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.LzGuimaraes.FocusLifeHub.Exceptions.ResourceNotFoundException;
import dev.LzGuimaraes.FocusLifeHub.Financas.dto.FinancasRequestDTO;
import dev.LzGuimaraes.FocusLifeHub.Financas.dto.FinancasResponseDTO;
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

    public Page<FinancasResponseDTO> getAllFinancas(Pageable pageable) {
        return financasRepository.findAll(pageable)
                .map(financasMapper::toResponse);
    }

    public FinancasResponseDTO getFinancaById(Long id) {
        return financasRepository.findById(id)
                .map(financasMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Conta financeira com ID " + id + " não encontrada"));
    }

    public FinancasResponseDTO createFinanca(FinancasRequestDTO dto) {
        UserModel user = userRepository.findById(dto.user_id())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + dto.user_id() + " não encontrado"));

        FinancasModel financa = financasMapper.toModel(dto, user);
        FinancasModel savedFinanca = financasRepository.save(financa);

        return financasMapper.toResponse(savedFinanca);
    }

    public FinancasResponseDTO updateFinanca(Long id, FinancasRequestDTO dto) {
        FinancasModel financa = financasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta financeira com ID " + id + " não encontrada para alteração"));

        if (dto.nome() != null && !dto.nome().isBlank()) {
            financa.setNome(dto.nome());
        }
        if (dto.moeda() != null && !dto.moeda().isBlank()) {
            financa.setMoeda(dto.moeda().toUpperCase());
        }

        if (dto.user_id() != null && !dto.user_id().equals(financa.getUser().getId())) {
            UserModel newUser = userRepository.findById(dto.user_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Novo usuário com ID " + dto.user_id() + " não encontrado"));
            financa.setUser(newUser);
        }
        FinancasModel updatedFinanca = financasRepository.save(financa);

        return financasMapper.toResponse(updatedFinanca);
    }

    public void deleteFinanca(Long id) {
        if (!financasRepository.existsById(id)) {
            throw new ResourceNotFoundException("Conta financeira com ID " + id + " não encontrada para exclusão");
        }
        financasRepository.deleteById(id);
    }
}