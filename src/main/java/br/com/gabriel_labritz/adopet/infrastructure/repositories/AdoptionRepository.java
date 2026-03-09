package br.com.gabriel_labritz.adopet.infrastructure.repositories;

import br.com.gabriel_labritz.adopet.enums.AdoptionStatus;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Adoption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
    Boolean existsByPetIdAndStatus(Long petId, AdoptionStatus status);
    Long countByTutorIdAndStatus(Long tutorId, AdoptionStatus status);
    Boolean existsByTutorIdAndPetIdAndStatusEquals(Long tutorId, Long petId, AdoptionStatus status);
}
