package br.com.gabriel_labritz.adopet.infrastructure.entities;

import br.com.gabriel_labritz.adopet.enums.AdoptionStatus;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "adocao")
public class Adoption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data", nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pet pet;

    @Column(name = "motivo", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdoptionStatus status;

    public Adoption(Tutor tutor, Pet pet, String reason) {
        this.tutor = tutor;
        this.pet = pet;
        this.reason = reason;
        this.date = LocalDate.now();
        this.status = AdoptionStatus.EM_ANDAMENTO;
    }

    public void approve() {
        if(!this.status.equals(AdoptionStatus.EM_ANDAMENTO)) {
            throw new AdoptionBusinessException(ErrosMessages.ADOPTION_CANNOT_BE_APPROVED.getErrorMessage());
        }

        this.status = AdoptionStatus.APROVADO;
        this.pet.markPetAsAdopted();
    }

    public void disapprove() {
        if(!this.status.equals(AdoptionStatus.EM_ANDAMENTO)) {
            throw new AdoptionBusinessException(ErrosMessages.ADOPTION_CANNOT_BE_REJECTED.getErrorMessage());
        }

        this.status = AdoptionStatus.REPROVADO;
    }
}
