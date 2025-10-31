package dev.LzGuimaraes.FocusLifeHub.Estudos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudosRepository extends JpaRepository<EstudosModel, Long> {
    Page<EstudosModel> findByMateria_UserId(Long userId, Pageable pageable);
}
