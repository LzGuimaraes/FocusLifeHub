package dev.LzGuimaraes.FocusLifeHub.Contas;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContasRepository extends JpaRepository<ContasModel, Long> {

    List<ContasModel> findByFinancasId(Long financasId);
}