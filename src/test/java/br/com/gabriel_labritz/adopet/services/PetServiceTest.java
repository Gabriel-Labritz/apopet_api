package br.com.gabriel_labritz.adopet.services;

import br.com.gabriel_labritz.adopet.dto.pets.PetRequestDto;
import br.com.gabriel_labritz.adopet.dto.pets.PetResponseDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterRequestDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterResponseDto;
import br.com.gabriel_labritz.adopet.enums.TypePet;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Pet;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Shelter;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.PetRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {
    @InjectMocks
    private PetService petService;

    @Mock
    private PetRepository petRepository;

    @Mock
    private ShelterService shelterService;

    private PetRequestDto dto;

    private Shelter shelter;

    private Pet pet;

    private PetResponseDto petResponseDto;

    @Captor
    private ArgumentCaptor<Pet> petCaptor;

    @Captor
    private ArgumentCaptor<Long> shelterIdCaptor;

    @BeforeEach
    void setUp() {
        this.dto = new PetRequestDto("Sophia", "Gato", "Siâmes", 15, 6.1, "Marrom", 1L);
        ShelterRequestDto shelterRequestDto = new ShelterRequestDto("teste@teste.com", "1187654321");

        this.shelter = new Shelter(shelterRequestDto);
        this.pet = new Pet(dto, shelter);

        this.petResponseDto = new PetResponseDto(
                pet.getId(),
                pet.getName(),
                pet.getType(),
                pet.getBreed(),
                pet.getAge(),
                pet.getWeight(), pet.getColor(),
                new ShelterResponseDto(shelter.getId(), shelterRequestDto.email(), shelter.getPhone()));
    }

    @Nested
    class registerPet {
        @Test
        @DisplayName("Deve chamar shelterService.findShelterEntityById passando o id do abrigo.")
        void shouldCallShelterServiceFindShelterEntityByIdWithShelterId() {
            // Arrange
            when(shelterService.findShelterEntityById(dto.shelterId())).thenReturn(shelter);
            when(petRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // Act
            petService.registerPet(dto);

            // Assert
            verify(shelterService).findShelterEntityById(shelterIdCaptor.capture());
            assertEquals(dto.shelterId(), shelterIdCaptor.getValue());
        }

        @Test
        @DisplayName("Deve chamar petRepository.save com os valores corretos.")
        void shouldCallPetRepositorySaveWithCorrectValues() {
            // Arrange
            when(shelterService.findShelterEntityById(dto.shelterId())).thenReturn(shelter);
            when(petRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // Act
            petService.registerPet(dto);

            // Assert
            verify(petRepository).save(petCaptor.capture());
            Pet petCaptured = petCaptor.getValue();

            assertEquals(dto.name(), petCaptured.getName());
            assertEquals(TypePet.toPetType(dto.type()), petCaptured.getType());
            assertEquals(dto.breed(), petCaptured.getBreed());
            assertEquals(dto.age(), petCaptured.getAge());
            assertEquals(dto.weight(), petCaptured.getWeight());
            assertEquals(dto.color(), petCaptured.getColor());
            assertEquals(shelter, petCaptured.getShelter());
        }

        @Test
        @DisplayName("Deve retornar PetResponseDto.")
        void shouldReturnPetResponseDto() {
            // Arrange
            when(shelterService.findShelterEntityById(dto.shelterId())).thenReturn(shelter);
            when(petRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // Act
            PetResponseDto result = petService.registerPet(dto);

            // Assert
            assertNotNull(result);
            assertEquals(petResponseDto, result);
        }

        @Test
        @DisplayName("Deve lançar uma NotFoundException quando o abrigo não for encontrado.")
        void shouldThrowNotFoundExceptionWhenShelterNotFound() {
            // Arrange
            when(shelterService.findShelterEntityById(dto.shelterId()))
                    .thenThrow(new NotFoundException(ErrosMessages.SHELTER_NOTFOUND.getErrorMessage()));

            // Act + Assert
            assertThrows(NotFoundException.class, () -> petService.registerPet(dto));
            verify(petRepository, never()).save(any());
        }
    }

    @Nested
    class getAllPets {
        @Test
        @DisplayName("Deve retornar todos os Pets com sucesso.")
        void shouldReturnAllPets() {
            // Arrange
            when(petRepository.findAll()).thenReturn(List.of(pet));

            // Act
            List<PetResponseDto> result = petService.getAllPets();

            // Assert
            verify(petRepository).findAll();
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(List.of(petResponseDto), result);
        }

        @Test
        @DisplayName("Deve retornar lista vazio quando não houver pets.")
        void shouldReturnEmptyListWhenNoPetsFound() {
            // Arrange
            when(petRepository.findAll()).thenReturn(List.of());

            // Act
            List<PetResponseDto> result = petService.getAllPets();

            // Assert
            verify(petRepository).findAll();
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
