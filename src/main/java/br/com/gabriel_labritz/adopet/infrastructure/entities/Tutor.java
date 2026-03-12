package br.com.gabriel_labritz.adopet.infrastructure.entities;

import br.com.gabriel_labritz.adopet.dto.tutor.TutorRequestDto;
import br.com.gabriel_labritz.adopet.dto.tutor.TutorUpdateDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@Entity
public class Tutor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "telefone", nullable = false)
    private String phone;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL)
    private List<Adoption> adoptions;

    public Tutor(TutorRequestDto tutorRequestDto) {
        this.name = tutorRequestDto.name();
        this.email = tutorRequestDto.email();
        this.phone = tutorRequestDto.phone();
    }

    public void updateTutor(TutorUpdateDto tutorUpdateDto) {
        Optional.ofNullable(tutorUpdateDto.email()).ifPresent(email -> this.email = email);
        Optional.ofNullable(tutorUpdateDto.phone()).ifPresent(phone -> this.phone = phone);
    }
}
