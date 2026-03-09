package br.com.gabriel_labritz.adopet.infrastructure.repositories;

import br.com.gabriel_labritz.adopet.infrastructure.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByAdoptedFalseOrderByIdDesc();
}
