package br.com.gabriel_labritz.adopet.infrastructure.entities;

import br.com.gabriel_labritz.adopet.enums.AdoptionStatus;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "adocao")
public class Adoption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "data", nullable = false)
    private LocalDate date;

    @Setter
    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    @Setter
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Setter
    @Column(name = "motivo", nullable = false)
    private String reason;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdoptionStatus status;

    public void approve() {
        if(!this.status.equals(AdoptionStatus.EM_ANDAMENTO)) {
            throw new AdoptionBusinessException("Essa adoção não pode ser aprovada.");
        }

        this.status = AdoptionStatus.APROVADO;
        this.pet.markPetAsAdopted();
    }

    public void disapprove() {
        if(!this.status.equals(AdoptionStatus.EM_ANDAMENTO)) {
            throw new AdoptionBusinessException("Essa adoção não pode ser reprovada.");
        }

        this.status = AdoptionStatus.REPROVADO;
    }
}
