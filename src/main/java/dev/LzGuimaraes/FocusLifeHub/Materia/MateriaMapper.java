package dev.LzGuimaraes.FocusLifeHub.Materia;

import org.springframework.stereotype.Component;

import dev.LzGuimaraes.FocusLifeHub.Materia.dto.MateriaRequestDTO;
import dev.LzGuimaraes.FocusLifeHub.Materia.dto.MateriaResponseDTO;

import dev.LzGuimaraes.FocusLifeHub.User.UserModel;

@Component
public class MateriaMapper {

    public MateriaModel toModel (MateriaRequestDTO dto, UserModel user) {
        MateriaModel materia = new MateriaModel();
        materia.setNome(dto.nome());
        materia.setDescricao(dto.descricao());
        return materia;
    }
    public MateriaResponseDTO toResponse(MateriaModel model) {
        return new MateriaResponseDTO(
            model.getId(),
            model.getNome(),
            model.getDescricao(),
            model.getUser().getId()    
        );
    }
}
