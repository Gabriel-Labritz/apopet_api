package br.com.gabriel_labritz.adopet.validations;

import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionRequestDto;
import br.com.gabriel_labritz.adopet.enums.AdoptionStatus;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.AdoptionRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutorAdoptionRejectedValidatorTest {
    @InjectMocks
    private TutorAdoptionRejectedValidator tutorAdoptionRejectedValidator;

    @Mock
    private AdoptionRepository adoptionRepository;

    private AdoptionRequestDto dto;

    @BeforeEach
    void setUp() {
        this.dto = new AdoptionRequestDto(1L, 3L, "Any reason here.");
    }

    @Nested
    class validate {
        @Test
        @DisplayName("Deve lançar uma AdoptionBusinessException quando o tutor já tem uma adoção reprovada pelo pet desejado.")
        void shouldThrowAdoptionBusinessExceptionWhenTutorHasRejectedAdoptionForPet() {
            // Arrange
            when(adoptionRepository.existsByTutorIdAndPetIdAndStatusEquals(dto.tutor_id(), dto.pet_id(), AdoptionStatus.REPROVADO))
                    .thenReturn(true);

            // Act + Assert
            assertThrows(AdoptionBusinessException.class, () -> tutorAdoptionRejectedValidator.validate(dto));
        }

        @Test
        @DisplayName("Não deve lançar uma AdoptionBusinessException quando o tutor ainda não teve uma adoção reprovada pelo pet desejado.")
        void shouldNotThrowAdoptionBusinessExceptionWhenTutorNotHasRejectedAdoptionForPet() {
            // Arrange
            when(adoptionRepository.existsByTutorIdAndPetIdAndStatusEquals(dto.tutor_id(), dto.pet_id(), AdoptionStatus.REPROVADO))
                    .thenReturn(false);

            // Act + Assert
            assertDoesNotThrow(() -> tutorAdoptionRejectedValidator.validate(dto));
        }

        @Test
        @DisplayName("Deve chamar existsByTutorIdAndPetIdAndStatusEquals com parâmetros corretos.")
        void shouldCallExistsByTutorIdAndPetIdAndStatusEqualsWithCorrectParams() {
            // Arrange
            when(adoptionRepository.existsByTutorIdAndPetIdAndStatusEquals(dto.tutor_id(), dto.pet_id(), AdoptionStatus.REPROVADO))
                    .thenReturn(false);

            // Act
            tutorAdoptionRejectedValidator.validate(dto);

            // Assert
            verify(adoptionRepository).existsByTutorIdAndPetIdAndStatusEquals(dto.tutor_id(),dto.pet_id(), AdoptionStatus.REPROVADO);
        }
    }
}