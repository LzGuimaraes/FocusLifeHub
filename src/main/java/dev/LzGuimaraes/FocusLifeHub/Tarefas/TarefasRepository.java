package dev.LzGuimaraes.FocusLifeHub.Tarefas;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefasRepository extends JpaRepository<TarefasModel, Long> {
    Page<TarefasModel> findByUserId(Long userId, Pageable pageable);
}
