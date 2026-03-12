package br.com.gabriel_labritz.adopet.infrastructure.entities;

import br.com.gabriel_labritz.adopet.dto.shelter.ShelterRequestDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterUpdateDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "abrigo")
public class Shelter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "telefone", nullable = false, unique = true)
    private String phone;

    @OneToMany(mappedBy = "shelter")
    private List<Pet> pets;

    public Shelter(ShelterRequestDto shelterRequestDto) {
        this.email = shelterRequestDto.email();
        this.phone = shelterRequestDto.phone();
    }

    public void updateShelter(ShelterUpdateDto shelterUpdateDto) {
        Optional.ofNullable(shelterUpdateDto.email()).ifPresent(email -> this.email = email);
        Optional.ofNullable(shelterUpdateDto.phone()).ifPresent(phone -> this.phone = phone);
    }
}
