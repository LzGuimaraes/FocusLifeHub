package dev.LzGuimaraes.Metas;

import org.springframework.stereotype.Component;

import dev.LzGuimaraes.FocusLifeHub.User.UserModel;
import dev.LzGuimaraes.Metas.dto.MetasRequestDTO;
import dev.LzGuimaraes.Metas.dto.MetasResponseDTO;

@Component
public class MetasMapper {
    
    public MetasModel toModel(MetasRequestDTO dto, UserModel user) {
        if (dto == null) {
            return null;
        }

        MetasModel meta = new MetasModel();
        meta.setTitulo(dto.titulo());
        meta.setDescricao(dto.descricao());
        meta.setPrograsso(dto.prograsso() != null ? dto.prograsso() : 0.0f);
        meta.setPrazo(dto.prazo());
        meta.setStatus(dto.status()); 
        meta.setUser(user);

        return meta;
    }
    public MetasResponseDTO toResponse(MetasModel meta) {
        if (meta == null) {
            return null;
        }
        Long userId = (meta.getUser() != null) ? meta.getUser().getId() : null;

        return new MetasResponseDTO(
            meta.getId(),
            meta.getTitulo(),
            meta.getDescricao(),
            meta.getPrograsso(),
            meta.getPrazo(),
            meta.getStatus(),
            userId
        );
    }
}