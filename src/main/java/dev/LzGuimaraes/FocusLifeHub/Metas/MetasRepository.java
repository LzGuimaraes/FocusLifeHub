package dev.LzGuimaraes.FocusLifeHub.Metas;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MetasRepository extends JpaRepository<MetasModel, Long> {
    Page<MetasModel> findByUserId(Long userId, Pageable pageable);
}