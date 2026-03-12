package br.com.gabriel_labritz.adopet.infrastructure.entities;

import br.com.gabriel_labritz.adopet.dto.pets.PetRequestDto;
import br.com.gabriel_labritz.adopet.dto.pets.UpdatePetDto;
import br.com.gabriel_labritz.adopet.enums.TypePet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TypePet type;

    @Column(name = "raca", nullable = false)
    private String breed;

    @Column(name = "idade", nullable = false)
    private Integer age;

    @Column(name = "peso")
    private Double weight;

    @Column(name = "cor", nullable = false)
    private String color;

    @Column(name = "adotado")
    private Boolean adopted = false;

    @OneToMany(mappedBy = "pet")
    private List<Adoption> adoption = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abrigo_id")
    private Shelter shelter;

    public Pet(PetRequestDto petRequestDto, Shelter shelter) {
        this.name = petRequestDto.name();
        this.type = TypePet.toPetType(petRequestDto.type());
        this.breed = petRequestDto.breed();
        this.age = petRequestDto.age();
        this.weight = petRequestDto.weight();
        this.color = petRequestDto.color();
        this.shelter = shelter;
    }

    public void updatePet(UpdatePetDto updatePetDto) {
        Optional.ofNullable(updatePetDto.age()).ifPresent(age -> this.age = age);
        Optional.ofNullable(updatePetDto.weight()).ifPresent(weight -> this.weight = weight);
    }

    public void changeShelter(Shelter shelter) {
       this.shelter = shelter;
    }

    public void markPetAsAdopted() {
        if(this.adopted) {
            throw new IllegalStateException("O pet já foi adotado");
        }

        this.adopted = true;
    }
}
