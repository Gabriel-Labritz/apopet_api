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
class TutorLimitAdoptionsValidatorTest {
    @InjectMocks
    private TutorLimitAdoptionsValidator tutorLimitAdoptionsValidator;

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
        @DisplayName("Deve lançar uma AdoptionBusinessException quando o tutor atingiu o limite de adoções aprovadas.")
        void shouldThrowAdoptionBusinessExceptionWhenTutorReachedLimitApprovedAdoptions() {
            // Arrange
            when(adoptionRepository.countByTutorIdAndStatus(dto.tutor_id(), AdoptionStatus.APROVADO)).thenReturn(3L);

            // Act + Assert
            assertThrows(AdoptionBusinessException.class, () -> tutorLimitAdoptionsValidator.validate(dto));
        }

        @Test
        @DisplayName("Não deve lançar uma AdoptionBusinessException quando o tutor ainda não atingiu o limite de adoções aprovadas.")
        void shouldNotThrowAdoptionBusinessExceptionWhenTutorNotReachedLimitApprovedAdoptions() {
            // Arrange
            when(adoptionRepository.countByTutorIdAndStatus(dto.tutor_id(), AdoptionStatus.APROVADO)).thenReturn(2L);

            // Act + Assert
            assertDoesNotThrow(() -> tutorLimitAdoptionsValidator.validate(dto));
        }

        @Test
        @DisplayName("Deve chamar countByTutorIdAndStatus com parâmetros corretos.")
        void shouldCallCountByTutorIdAndStatusWithCorrectParams() {
            // Arrange
            when(adoptionRepository.countByTutorIdAndStatus(dto.tutor_id(), AdoptionStatus.APROVADO)).thenReturn(2L);

            // Act
            tutorLimitAdoptionsValidator.validate(dto);

            // Assert
            verify(adoptionRepository).countByTutorIdAndStatus(dto.tutor_id(), AdoptionStatus.APROVADO);
        }
    }
}