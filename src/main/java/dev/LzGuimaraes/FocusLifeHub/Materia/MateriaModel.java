package dev.LzGuimaraes.FocusLifeHub.Materia;


import dev.LzGuimaraes.FocusLifeHub.User.UserModel;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tb_materia")
public class MateriaModel {

    private Long id;
    private String nome;
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel User;

}
