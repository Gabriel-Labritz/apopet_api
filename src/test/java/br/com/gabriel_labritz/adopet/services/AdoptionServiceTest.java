package br.com.gabriel_labritz.adopet.services;

import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionRequestDto;
import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionResponseDto;
import br.com.gabriel_labritz.adopet.enums.AdoptionStatus;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Adoption;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Pet;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Tutor;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.AdoptionRepository;
import br.com.gabriel_labritz.adopet.validations.AdoptionValidator;
import br.com.gabriel_labritz.adopet.validations.PetAlreadyAdoptedValidator;
import br.com.gabriel_labritz.adopet.validations.TutorLimitAdoptionsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdoptionServiceTest {
    @InjectMocks
    private AdoptionService adoptionService;

    @Mock
    private AdoptionRepository adoptionRepository;

    @Mock
    private TutorService tutorService;

    @Mock
    private PetService petService;

    @Mock
    private Tutor tutor;

    @Mock
    private Pet pet;

    private AdoptionRequestDto dto;

    private Adoption adoption;

    @Spy
    private List<AdoptionValidator> validators = new ArrayList<>();

    @Mock
    private PetAlreadyAdoptedValidator petAlreadyAdoptedValidator;

    @Mock
    private TutorLimitAdoptionsValidator tutorLimitAdoptionsValidator;

    @Captor
    private ArgumentCaptor<Adoption> adoptionCaptor;

    private AdoptionResponseDto adoptionResponseDto;

    @BeforeEach
    void setUp() {
        this.dto = new AdoptionRequestDto(2L, 4L, "Motivo...");
        this.adoption = new Adoption(tutor, pet, dto.reason());
        this.adoptionResponseDto = new AdoptionResponseDto(null, LocalDate.now(), dto.reason(), AdoptionStatus.EM_ANDAMENTO);
    }

    @Nested
    class adopetPet {
        @Test
        @DisplayName("Deve lançar uma NotFoundException quando o tutor não for encontrado.")
        void shouldThrowNotFoundExceptionWhenTutorNotFound() {
            // Arrange
            when(tutorService.findTutorEntityById(dto.tutor_id()))
                    .thenThrow(new NotFoundException(ErrosMessages.TUTOR_NOTFOUND.getErrorMessage()));

            // Act + Assert
            assertThrows(NotFoundException.class, () -> adoptionService.adopetPet(dto));
        }

        @Test
        @DisplayName("Deve lançar uma NotFoundException quando o pet não for encontrado.")
        void shouldThrowNotFoundExceptionWhenPetNotFound() {
            // Arrange
            when(petService.findPetEntityById(dto.pet_id()))
                    .thenThrow(new NotFoundException(ErrosMessages.PET_NOTFOUND.getErrorMessage()));

            // Act + Assert
            assertThrows(NotFoundException.class, () -> adoptionService.adopetPet(dto));
        }

        @Test
        @DisplayName("Deve chamar as validações quando a solicitação de adoção ocorrer.")
        void shouldCallValidationsWhenAdoptionOccurs() {
            // Arrange
            when(tutorService.findTutorEntityById(dto.tutor_id())).thenReturn(tutor);
            when(petService.findPetEntityById(dto.pet_id())).thenReturn(pet);
            when(adoptionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            validators.add(petAlreadyAdoptedValidator);
            validators.add(tutorLimitAdoptionsValidator);

            // Act
            adoptionService.adopetPet(dto);

            // Assert
            verify(petAlreadyAdoptedValidator, times(1)).validate(dto);
            verify(tutorLimitAdoptionsValidator, times(1)).validate(dto);
        }

        @Test
        @DisplayName("Deve lançar uma AdoptionBusinessException quando alguma validação falhar.")
        void shouldThrowAdoptionBusinessExceptionWhenSomeValidationFails() {
            // Arrange
            when(tutorService.findTutorEntityById(dto.tutor_id())).thenReturn(tutor);
            when(petService.findPetEntityById(dto.pet_id())).thenReturn(pet);
            doThrow(new AdoptionBusinessException(ErrosMessages.PET_ADOPTED.getErrorMessage())).when(petAlreadyAdoptedValidator).validate(dto);

            validators.add(petAlreadyAdoptedValidator);

            // Act + Assert
            assertThrows(AdoptionBusinessException.class, () -> adoptionService.adopetPet(dto));
            verify(adoptionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve salvar a solicitação de adoção com sucesso.")
        void shouldSaveAdoptionSolicitation() {
            // Arrange
            when(tutorService.findTutorEntityById(dto.tutor_id())).thenReturn(tutor);
            when(petService.findPetEntityById(dto.pet_id())).thenReturn(pet);
            when(adoptionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // Act
            adoptionService.adopetPet(dto);

            // Assert
            verify(tutorService).findTutorEntityById(dto.tutor_id());
            verify(petService).findPetEntityById(dto.pet_id());
            verify(adoptionRepository).save(adoptionCaptor.capture());
            Adoption adoptionCaptured = adoptionCaptor.getValue();

            assertEquals(tutor, adoptionCaptured.getTutor());
            assertEquals(pet, adoptionCaptured.getPet());
            assertEquals(dto.reason(), adoptionCaptured.getReason());
        }

        @Test
        @DisplayName("Deve retornar AdoptionResponseDto.")
        void shouldReturnAdoptionResponseDto() {
            // Arrange
            when(tutorService.findTutorEntityById(dto.tutor_id())).thenReturn(tutor);
            when(petService.findPetEntityById(dto.pet_id())).thenReturn(pet);
            when(adoptionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // Act
            AdoptionResponseDto result = adoptionService.adopetPet(dto);

            // Assert
            assertEquals(adoptionResponseDto.reason(), result.reason());
            assertEquals(AdoptionStatus.EM_ANDAMENTO, result.status());
            assertEquals(LocalDate.now(), result.date());
        }
    }

    @Nested
    class getAllAdoptions {
        @Test
        @DisplayName("Deve retornar todas as solicitações de adoções com sucesso.")
        void shouldReturnAllAdoptionSolicitations() {
            // Arrange
            when(adoptionRepository.findAll()).thenReturn(List.of(adoption));

            // Act
            List<AdoptionResponseDto> result = adoptionService.getAllAdoptions();

            // Assert
            verify(adoptionRepository).findAll();
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(List.of(adoptionResponseDto), result);
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia quando não houver solicitações de adoção.")
        void shouldReturnEmptyListWhenAdoptionSolicitationsNotFound() {
            // Arrange
            when(adoptionRepository.findAll()).thenReturn(List.of());

            // Act
            List<AdoptionResponseDto> result = adoptionService.getAllAdoptions();

            // Assert
            verify(adoptionRepository).findAll();
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class getAdoptionById {
        @Test
        @DisplayName("Deve retornar uma solicitação de adoção pelo id.")
        void shouldReturnAAdoptionSolicitationById() {
            // Arrange
            Long adoptionId = 1L;
            when(adoptionRepository.findById(adoptionId)).thenReturn(Optional.of(adoption));

            // Act
            AdoptionResponseDto result = adoptionService.getAdoptionById(adoptionId);

            // Assert
            verify(adoptionRepository).findById(adoptionId);
            assertNotNull(result);

            assertEquals(adoptionResponseDto.date(), result.date());
            assertEquals(adoptionResponseDto.reason(), result.reason());
            assertEquals(adoptionResponseDto.status(), result.status());
        }

        @Test
        @DisplayName("Deve lançar uma NotFoundException quando a solicitação de adoção não for encontrada.")
        void shouldThrowNotFoundExceptionWhenAdoptionSolicitationNotFound() {
            // Arrange
            Long adoptionId = 1L;
            when(adoptionRepository.findById(adoptionId)).thenReturn(Optional.empty());

            // Act
            assertThrows(NotFoundException.class, () -> adoptionService.getAdoptionById(adoptionId));
        }
    }
}