package br.com.gabriel_labritz.adopet.infrastructure.repositories;

import br.com.gabriel_labritz.adopet.infrastructure.entities.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    Boolean existsByEmail(String email);
}
