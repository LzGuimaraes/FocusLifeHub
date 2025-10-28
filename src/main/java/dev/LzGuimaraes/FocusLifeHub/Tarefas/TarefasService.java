package dev.LzGuimaraes.FocusLifeHub.Tarefas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.LzGuimaraes.FocusLifeHub.Exceptions.ResourceNotFoundException;
import dev.LzGuimaraes.FocusLifeHub.Tarefas.Enum.Prioridade;
import dev.LzGuimaraes.FocusLifeHub.Tarefas.Enum.TarefaStatus;
import dev.LzGuimaraes.FocusLifeHub.Tarefas.dto.TarefasRequestDTO;
import dev.LzGuimaraes.FocusLifeHub.Tarefas.dto.TarefasResponseDTO;
import dev.LzGuimaraes.FocusLifeHub.User.UserModel;
import dev.LzGuimaraes.FocusLifeHub.User.UserRepository;

@Service
public class TarefasService {

    private final TarefasRepository tarefasRepository;
    private final UserRepository userRepository;
    private final TarefasMapper tarefasMapper;

    public TarefasService( TarefasRepository tarefasRepository, UserRepository userRepository, TarefasMapper tarefasMapper) {

                this.tarefasRepository = tarefasRepository;
                this.userRepository = userRepository;
                this.tarefasMapper = tarefasMapper;
            }

    public Page<TarefasResponseDTO> getAllTarefas(Pageable pageable) {
        return tarefasRepository.findAll(pageable)
                .map(tarefasMapper::toResponse);
    }

    public TarefasResponseDTO getTarefaById(Long id) {
        return tarefasRepository.findById(id)
                .map(tarefasMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa com ID " + id + " não encontrada"));
    }

    public TarefasResponseDTO createTarefa(TarefasRequestDTO dto) {
        UserModel user = userRepository.findById(dto.user_id())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + dto.user_id() + " não encontrado"));

        TarefasModel tarefa = tarefasMapper.toModel(dto, user);
        tarefa.setStatus(TarefaStatus.PENDENTE);
        
        if (dto.prioridade() == null) {
            tarefa.setPrioridade(Prioridade.media); 
        }

        TarefasModel savedTarefa = tarefasRepository.save(tarefa);

        return tarefasMapper.toResponse(savedTarefa);
    }

    public TarefasResponseDTO updateTarefa(Long id, TarefasRequestDTO dto) {
        TarefasModel tarefa = tarefasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa com ID " + id + " não encontrada para alteração"));

        if (dto.titulo() != null && !dto.titulo().isBlank()) {
            tarefa.setTitulo(dto.titulo());
        }
        if (dto.status() != null) {
            tarefa.setStatus(dto.status());
        }
        if (dto.prioridade() != null) {
            tarefa.setPrioridade(dto.prioridade());
        }
        if (dto.prazo() != null) {
            tarefa.setPrazo(dto.prazo());
        }

        if (dto.user_id() != null && !dto.user_id().equals(tarefa.getUser().getId())) {
            UserModel newUser = userRepository.findById(dto.user_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Novo usuário com ID " + dto.user_id() + " não encontrado"));
            tarefa.setUser(newUser);
        }

        TarefasModel updatedTarefa = tarefasRepository.save(tarefa);

        return tarefasMapper.toResponse(updatedTarefa);
    }

    public void deleteTarefa(Long id) {
        if (!tarefasRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarefa com ID " + id + " não encontrada para exclusão");
        }
        tarefasRepository.deleteById(id);
    }
}