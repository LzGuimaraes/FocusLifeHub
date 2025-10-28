package dev.LzGuimaraes.FocusLifeHub.Materia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.LzGuimaraes.FocusLifeHub.Exceptions.ResourceNotFoundException;
import dev.LzGuimaraes.FocusLifeHub.Materia.dto.MateriaRequestDTO;
import dev.LzGuimaraes.FocusLifeHub.Materia.dto.MateriaResponseDTO;
import dev.LzGuimaraes.FocusLifeHub.User.UserModel;
import dev.LzGuimaraes.FocusLifeHub.User.UserRepository;

@Service
public class MateriaService {
    private MateriaRepository materiaRepository;
    private UserRepository userRepository;
    private MateriaMapper materiaMapper;

    public MateriaService(MateriaRepository materiaRepository,UserRepository userRepository,MateriaMapper materiaMapper ) {
            this.materiaMapper = materiaMapper;
            this.materiaRepository = materiaRepository;
            this.userRepository = userRepository;
    }

    public Page<MateriaResponseDTO> getAllMaterias(Pageable pageable) {
        return materiaRepository.findAll(pageable)
                .map(materiaMapper::toResponse);
    }

    public MateriaResponseDTO getMateriaById(Long id) {
        return materiaRepository.findById(id)
                .map(materiaMapper::toResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Aulas com ID " + id + "não encontrada"));
    }

    public MateriaResponseDTO createMateria(MateriaRequestDTO dto) {
        UserModel user = userRepository.findById(dto.user_id())
                    .orElseThrow(()-> new ResourceNotFoundException("Usuario com ID" + dto.user_id() + "Não encontrada"));

                    MateriaModel materia = materiaMapper.toModel(dto, user);
                    MateriaModel saved = materiaRepository.save(materia);

                    return materiaMapper.toResponse(saved);
            }

    public MateriaResponseDTO alterarMateria(Long id, MateriaRequestDTO dto) {
        MateriaModel materia = materiaRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Materia com ID" + id + "Não encontrada para alteração"));

            if (dto.nome() != null && !dto.nome().isBlank()) {
            materia.setNome(dto.nome());
            }

            if (dto.descricao() != null) {
            materia.setDescricao(dto.descricao());
            }

            if (dto.user_id() != null) {
            UserModel newUser = userRepository.findById(dto.user_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario com ID " + dto.user_id() + " não encontrado para associação"));
            materia.setUser(newUser); 
        }
        MateriaModel alterarMateria = materiaRepository.save(materia);
        return materiaMapper.toResponse(alterarMateria);
    }
    
    
    public void deleteMateria(Long id) {
        if(!materiaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Materia com ID" + id + "não encontrada para exclusão");
        }
        materiaRepository.deleteById(id);
    }
}

    
