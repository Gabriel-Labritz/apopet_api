package br.com.gabriel_labritz.adopet.infrastructure.repositories;

import br.com.gabriel_labritz.adopet.infrastructure.entities.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelterRepository extends JpaRepository<Shelter, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
}
