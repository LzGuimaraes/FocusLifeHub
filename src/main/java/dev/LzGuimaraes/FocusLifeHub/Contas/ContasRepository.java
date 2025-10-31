package dev.LzGuimaraes.FocusLifeHub.Contas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@Repository
public interface ContasRepository extends JpaRepository<ContasModel, Long> {
    Page<ContasModel> findByFinancas_UserId(Long userId, Pageable pageable);
}