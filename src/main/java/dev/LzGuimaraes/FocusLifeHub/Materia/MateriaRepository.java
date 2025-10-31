package dev.LzGuimaraes.FocusLifeHub.Materia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaRepository extends JpaRepository <MateriaModel, Long> {
    Page<MateriaModel> findByUserId(Long userId, Pageable pageable);
}
