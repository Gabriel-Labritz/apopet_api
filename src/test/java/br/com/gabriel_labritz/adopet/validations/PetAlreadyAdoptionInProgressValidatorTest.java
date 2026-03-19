package br.com.gabriel_labritz.adopet.validations;

import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionRequestDto;
import br.com.gabriel_labritz.adopet.enums.AdoptionStatus;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.AdoptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetAlreadyAdoptionInProgressValidatorTest {
    @InjectMocks
    private PetAlreadyAdoptionInProgressValidator petAlreadyAdoptionInProgressValidator;

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
        @DisplayName("Deve lançar uma AdoptionBusinessException para um pet que já está com uma adoção em andamento.")
        void shouldThrowAdoptionBusinessExceptionWhenPetAlreadyAdoptionInProgress() {
            // Arrange
            when(adoptionRepository.existsByPetIdAndStatus(dto.pet_id(), AdoptionStatus.EM_ANDAMENTO)).thenReturn(true);

            // Act + Assert
            assertThrows(AdoptionBusinessException.class, () -> petAlreadyAdoptionInProgressValidator.validate(dto));
        }

        @Test
        @DisplayName("Não deve lançar uma AdoptionBusinessException para um pet que não está com uma adoção em andamento.")
        void shouldNotThrowAdoptionBusinessExceptionWhenPetIsNotAdoptionInProgress() {
            // Arrange
            when(adoptionRepository.existsByPetIdAndStatus(dto.pet_id(), AdoptionStatus.EM_ANDAMENTO)).thenReturn(false);

            // Act + Assert
            assertDoesNotThrow(() -> petAlreadyAdoptionInProgressValidator.validate(dto));
        }

        @Test
        @DisplayName("Deve chamar existsByPetIdAndStatus com parâmetros corretos.")
        void shouldCallExistsByPetIdAndStatusWithCorrectParams() {
            // Arrange
            when(adoptionRepository.existsByPetIdAndStatus(dto.pet_id(), AdoptionStatus.EM_ANDAMENTO)).thenReturn(false);

            // Act
            petAlreadyAdoptionInProgressValidator.validate(dto);

            // Assert
            verify(adoptionRepository).existsByPetIdAndStatus(dto.pet_id(), AdoptionStatus.EM_ANDAMENTO);
        }
    }
}