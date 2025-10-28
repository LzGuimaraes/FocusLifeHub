package dev.LzGuimaraes.FocusLifeHub.Metas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.LzGuimaraes.FocusLifeHub.Exceptions.ResourceNotFoundException;
import dev.LzGuimaraes.FocusLifeHub.Metas.Enum.MetaStatus;
import dev.LzGuimaraes.FocusLifeHub.Metas.dto.MetasRequestDTO;
import dev.LzGuimaraes.FocusLifeHub.Metas.dto.MetasResponseDTO;
import dev.LzGuimaraes.FocusLifeHub.User.UserModel;
import dev.LzGuimaraes.FocusLifeHub.User.UserRepository;

@Service
public class MetasService {

    private final MetasRepository metasRepository;
    private final UserRepository userRepository; 
    private final MetasMapper metasMapper;

    public MetasService( MetasRepository metasRepository, UserRepository userRepository, MetasMapper metasMapper) {
                
        this.metasRepository = metasRepository;
        this.userRepository = userRepository;
        this.metasMapper = metasMapper;
        
    }

    public Page<MetasResponseDTO> getAllMetas(Pageable pageable) {
        return metasRepository.findAll(pageable)
                .map(metasMapper::toResponse);
    }

    public MetasResponseDTO getMetaById(Long id) {
        return metasRepository.findById(id)
                .map(metasMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Meta com ID " + id + " não encontrada"));
    }

    public MetasResponseDTO createMeta(MetasRequestDTO dto) {
        UserModel user = userRepository.findById(dto.user_id())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + dto.user_id() + " não encontrado"));

        MetasModel meta = metasMapper.toModel(dto, user);

        meta.setStatus(MetaStatus.PENDENTE); 
        meta.setPrograsso(0.0f); 

        MetasModel savedMeta = metasRepository.save(meta);

        return metasMapper.toResponse(savedMeta);
    }

    public MetasResponseDTO updateMeta(Long id, MetasRequestDTO dto) {

        MetasModel meta = metasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta com ID " + id + " não encontrada para alteração"));

        if (dto.titulo() != null && !dto.titulo().isBlank()) {
            meta.setTitulo(dto.titulo());
        }
        if (dto.descricao() != null) {
            meta.setDescricao(dto.descricao());
        }
        if (dto.prograsso() != null) {
            meta.setPrograsso(dto.prograsso());
        }
        if (dto.prazo() != null) {
            meta.setPrazo(dto.prazo());
        }
        if (dto.status() != null) {
            meta.setStatus(dto.status());
        }

        if (dto.user_id() != null && !dto.user_id().equals(meta.getUser().getId())) {
            UserModel newUser = userRepository.findById(dto.user_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Novo usuário com ID " + dto.user_id() + " não encontrado"));
            meta.setUser(newUser);
        }

        MetasModel updatedMeta = metasRepository.save(meta);

        return metasMapper.toResponse(updatedMeta);
    }

    public void deleteMeta(Long id) {
        if (!metasRepository.existsById(id)) {
            throw new ResourceNotFoundException("Meta com ID " + id + " não encontrada para exclusão");
        }
        metasRepository.deleteById(id);
    }
}