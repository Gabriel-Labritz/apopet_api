package br.com.gabriel_labritz.adopet.infrastructure.entities;

import br.com.gabriel_labritz.adopet.enums.TypePet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "nome", nullable = false)
    private String name;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TypePet type;

    @Setter
    @Column(name = "raca", nullable = false)
    private String breed;

    @Setter
    @Column(name = "idade", nullable = false)
    private Integer age;

    @Setter
    @Column(name = "peso")
    private Double weight;

    @Setter
    @Column(name = "cor", nullable = false)
    private String color;

    @Setter
    @Column(name = "adotado")
    private Boolean adopted = false;

    @Setter
    @OneToMany(mappedBy = "pet")
    private List<Adoption> adoption = new ArrayList<>();

    @Setter
    @ManyToOne
    @JoinColumn(name = "abrigo_id")
    private Shelter shelter;

    public void markPetAsAdopted() {
        if(this.adopted) {
            throw new IllegalStateException("O pet já foi adotado");
        }

        this.adopted = true;
    }
}
