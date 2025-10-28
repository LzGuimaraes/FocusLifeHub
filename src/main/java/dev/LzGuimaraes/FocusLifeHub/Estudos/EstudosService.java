package dev.LzGuimaraes.FocusLifeHub.Estudos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.LzGuimaraes.FocusLifeHub.Exceptions.ResourceNotFoundException;
import dev.LzGuimaraes.FocusLifeHub.Estudos.dto.EstudosRequestDTO;
import dev.LzGuimaraes.FocusLifeHub.Estudos.dto.EstudosResponseDTO;
import dev.LzGuimaraes.FocusLifeHub.Materia.MateriaModel;
import dev.LzGuimaraes.FocusLifeHub.Materia.MateriaRepository;

@Service
public class EstudosService {

    private final EstudosRepository estudosRepository;
    private final MateriaRepository materiaRepository;
    private final EstudosMapper estudosMapper;

    public EstudosService(
            EstudosRepository estudosRepository, 
            MateriaRepository materiaRepository, 
            EstudosMapper estudosMapper) {
        this.estudosRepository = estudosRepository;
        this.materiaRepository = materiaRepository;
        this.estudosMapper = estudosMapper;
    }

    public Page<EstudosResponseDTO> getAllEstudos(Pageable pageable) {
        return estudosRepository.findAll(pageable)
                .map(estudosMapper::toResponse);
    }

    public EstudosResponseDTO getEstudosById(Long id) {
        return estudosRepository.findById(id)
                .map(estudosMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de estudo com ID " + id + " não encontrada"));
    }

    public EstudosResponseDTO createEstudos(EstudosRequestDTO dto) {
        MateriaModel materia = materiaRepository.findById(dto.materia_id())
                .orElseThrow(() -> new ResourceNotFoundException("Matéria com ID " + dto.materia_id() + " não encontrada"));

        EstudosModel estudo = estudosMapper.toModel(dto, materia);
        EstudosModel savedEstudo = estudosRepository.save(estudo);
        return estudosMapper.toResponse(savedEstudo);
    }

    public EstudosResponseDTO updateEstudos(Long id, EstudosRequestDTO dto) {
        EstudosModel estudo = estudosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de estudo com ID " + id + " não encontrada para alteração"));

        if (dto.nome() != null && !dto.nome().isBlank()) {
            estudo.setNome(dto.nome());
        }
        if (dto.duracao_min() != null && dto.duracao_min() > 0) {
            estudo.setDuracao_min(dto.duracao_min());
        }
        if (dto.data() != null) {
            estudo.setData(dto.data());
        }
        if (dto.notas() != null) {
            estudo.setNotas(dto.notas());
        }

        if (dto.materia_id() != null) {
            MateriaModel novaMateria = materiaRepository.findById(dto.materia_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Nova matéria com ID " + dto.materia_id() + " não encontrada"));
            estudo.setMateria(novaMateria);
        }

        EstudosModel updatedEstudo = estudosRepository.save(estudo);

        return estudosMapper.toResponse(updatedEstudo);
    }

    public void deleteEstudos(Long id) {
        if (!estudosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sessão de estudo com ID " + id + " não encontrada para exclusão");
        }
        estudosRepository.deleteById(id);
    }
}