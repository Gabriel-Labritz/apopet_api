package br.com.gabriel_labritz.adopet.validations;

import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionRequestDto;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Pet;
import br.com.gabriel_labritz.adopet.services.PetService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetAlreadyAdoptedValidatorTest {

    @InjectMocks
    private PetAlreadyAdoptedValidator petAlreadyAdoptedValidator;

    @Mock
    private PetService petService;

    @Mock
    private Pet pet;

    private AdoptionRequestDto dto;

    @BeforeEach
    void setUp() {
        this.dto = new AdoptionRequestDto(1L, 3L, "Any reason here.");
    }

    @Nested
    class validate {
        @Test
        @DisplayName("Deve lançar uma AdoptionBusinessException quando o pet já foi adotado.")
        void shouldThrowAdoptionBusinessExceptionWhenPetAlreadyAdopted() {
            // Arrange
            when(petService.findPetEntityById(dto.pet_id())).thenReturn(pet);
            when(pet.getAdopted()).thenReturn(true);

            //  Act + Assert
            Assertions.assertThrows(AdoptionBusinessException.class, () -> petAlreadyAdoptedValidator.validate(dto));
        }

        @Test
        @DisplayName("Não deve lançar uma AdoptionBusinessException quando o pet não está adotado.")
        void shouldNotThrowAdoptionBusinessExceptionWhenPetIsNotAdopted() {
            // Arrange
            when(petService.findPetEntityById(dto.pet_id())).thenReturn(pet);
            when(pet.getAdopted()).thenReturn(false);

            //  Act + Assert
            Assertions.assertDoesNotThrow(() -> petAlreadyAdoptedValidator.validate(dto));
        }

        @Test
        @DisplayName("Deve chamar petService.findPetEntityById ao menos uma vez e com paramêtros corretos.")
        void shouldCallFindPetEntityByIdWithCorrectParams() {
            // Arrange
            when(petService.findPetEntityById(dto.pet_id())).thenReturn(pet);
            when(pet.getAdopted()).thenReturn(false);

            //  Act
            petAlreadyAdoptedValidator.validate(dto);

            // Assert
            verify(petService).findPetEntityById(dto.pet_id());
        }
    }
}