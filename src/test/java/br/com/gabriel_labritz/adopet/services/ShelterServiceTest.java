package br.com.gabriel_labritz.adopet.services;

import br.com.gabriel_labritz.adopet.dto.shelter.ShelterRequestDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterResponseDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterUpdateDto;
import br.com.gabriel_labritz.adopet.exceptions.DuplicationExistsException;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Shelter;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.ShelterRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelterServiceTest {
    @InjectMocks
    private ShelterService shelterService;

    @Mock
    private ShelterRepository shelterRepository;

    private ShelterRequestDto dto;

    private ShelterResponseDto shelterResponseDto;

    private Shelter shelter;

    @Captor
    private ArgumentCaptor<Shelter> shelterCaptor;

    @BeforeEach
    void setUp() {
         this.dto = new ShelterRequestDto("shelter@email.com", "1123456789");
         this.shelter = new Shelter(dto);
         this.shelterResponseDto = new ShelterResponseDto(shelter.getId(), dto.email(), dto.phone());
    }

    @Nested
    class shelterRegister {
        @Test
        @DisplayName("Deve chamar existsByEmailOrPhone")
        void shouldCallExistsByEmailOrPhone() {
            // Arrange
            when(shelterRepository.existsByEmailOrPhone(any(), any())).thenReturn(false);

            // Act
            shelterService.shelterRegister(dto);

            // Assert
            verify(shelterRepository).existsByEmailOrPhone(dto.email(), dto.phone());
        }

        @Test
        @DisplayName("Deve lançar uma DuplicationExistsException quando o email ou telefone do abrigo já está em uso.")
        void shouldThrowDuplicationExistsExceptionWhenShelterEmailOrPhoneAlreadyUsed() {
            // Arrange
            when(shelterRepository.existsByEmailOrPhone(dto.email(), dto.phone())).thenReturn(true);

            // Act + Assert
           assertThrows(DuplicationExistsException.class, () -> shelterService.shelterRegister(dto));
        }

        @Test
        @DisplayName("Deve chamar shelterRepository.save com os valores corretos")
        void shouldCallShelterRepositorySaveWithCorrectValues() {
            // Arrange
            when(shelterRepository.existsByEmailOrPhone(any(), any())).thenReturn(false);
            when(shelterRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // Act
            shelterService.shelterRegister(dto);

            // Assert
            verify(shelterRepository).save(shelterCaptor.capture());
            Shelter shelterCaptured = shelterCaptor.getValue();

            assertEquals(dto.email(), shelterCaptured.getEmail());
            assertEquals(dto.phone(), shelterCaptured.getPhone());
        }

        @Test
        @DisplayName("Deve retornar ShelterResponseDto")
        void shouldReturnShelterResponseDto() {
            // Arrange
            when(shelterRepository.existsByEmailOrPhone(any(), any())).thenReturn(false);
            when(shelterRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // Act
            ShelterResponseDto result = shelterService.shelterRegister(dto);

            // Assert
            assertEquals(shelterResponseDto, result);
        }
    }

    @Nested
    class findShelterEntityById {
        @Test
        @DisplayName("Deve lançar uma NotFoundException quando o abrigo não for encontrado.")
        void shouldThrowNotFoundExceptionWhenShelterNotFound() {
            // Arrange
            Long shelterId = 1L;
            when(shelterRepository.findById(shelterId)).thenReturn(Optional.empty());

            // Act + Assert
            assertThrows(NotFoundException.class, () -> shelterService.getShelterById(shelterId));
        }

        @Test
        @DisplayName("Deve retornar com sucesso um abrigo pelo id.")
        void shouldReturnAShelterById() {
            // Arrange
            Long shelterId = 1L;
            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));

            // Act
            Shelter result = shelterService.findShelterEntityById(shelterId);

            // Assert
            assertNotNull(result);
            assertEquals(shelter, result);
        }
    }

    @Nested
    class getShelterById {
        @Test
        @DisplayName("Deve retornar um abrigo.")
        void shouldReturnAShelter() {
            // Arrange
            Long shelterId = 1L;
            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));

            // Act
            ShelterResponseDto result = shelterService.getShelterById(shelterId);

            // Assert
            verify(shelterRepository).findById(shelterId);
            assertNotNull(result);
            assertEquals(shelterResponseDto, result);
        }
    }

    @Nested
    class updateShelterById {
        @Test
        @DisplayName("Não deve chamar existsByEmail quando o email não for enviado.")
        void shouldNotCallExistsByEmailWhenEmailWasNotSend() {
            // Arrange
            Long shelterId = 1L;
            ShelterUpdateDto dtoUpdate = new ShelterUpdateDto(null, "1198765432");

            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));
            when(shelterRepository.save(any())).thenReturn(shelter);

            // Act
            shelterService.updateShelterById(shelterId, dtoUpdate);

            // Assert
            verify(shelterRepository, never()).existsByEmail(any());
            verify(shelterRepository).save(any());
        }

        @Test
        @DisplayName("Não deve chamar existsByEmail quando o email que será atualizado é o mesmo do que já está sendo usado pelo abrigo.")
        void shouldNotCallExistsByEmailWhenEmailShelterIsEquals() {
            // Arrange
            Long shelterId = 1L;
            ShelterUpdateDto dtoUpdate = new ShelterUpdateDto(shelter.getEmail(), "1198765432");

            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));
            when(shelterRepository.save(any())).thenReturn(shelter);

            // Act
            shelterService.updateShelterById(shelterId, dtoUpdate);

            // Assert
            verify(shelterRepository, never()).existsByEmail(any());
            verify(shelterRepository).save(any());
        }

        @Test
        @DisplayName("Deve lançar uma DuplicationExistsException quando o email do abrigo já está em uso.")
        void shouldThrowDuplicationExistsExceptionWhenShelterEmailIsAlreadyUsed() {
            // Arrange
            Long shelterId = 1L;
            ShelterUpdateDto dtoUpdate = new ShelterUpdateDto("shelterupdateemail@email.com", "1198765432");

            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));
            when(shelterRepository.existsByEmail(dtoUpdate.email())).thenReturn(true);

            // Act
            assertThrows(DuplicationExistsException.class, () -> shelterService.updateShelterById(shelterId, dtoUpdate));
        }

        @Test
        @DisplayName("Não deve lançar uma DuplicationExistsException quando o email do abrigo ainda não está em uso.")
        void shouldNotThrowDuplicationExistsExceptionWhenShelterEmailIsNotUsed() {
            // Arrange
            Long shelterId = 1L;
            ShelterUpdateDto dtoUpdate = new ShelterUpdateDto("shelterupdateemail@email.com", "1198765432");

            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));
            when(shelterRepository.existsByEmail(dtoUpdate.email())).thenReturn(false);
            when(shelterRepository.save(any())).thenReturn(shelter);

            // Act
            assertDoesNotThrow(() -> shelterService.updateShelterById(shelterId, dtoUpdate));
        }

        @Test
        @DisplayName("Não deve chamar existsByPhone quando o telefone não for enviado.")
        void shouldNotCallExistsByPhoneWhenPhoneWasNotSend() {
            // Arrange
            Long shelterId = 1L;
            ShelterUpdateDto dtoUpdate = new ShelterUpdateDto("shelterupdateemail@email.com", null);

            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));
            when(shelterRepository.save(any())).thenReturn(shelter);

            // Act
            shelterService.updateShelterById(shelterId, dtoUpdate);

            // Assert
            verify(shelterRepository, never()).existsByPhone(any());
            verify(shelterRepository).save(any());
        }

        @Test
        @DisplayName("Não deve chamar existsByPhone quando o telefone que será atualizado é o mesmo do que já está sendo usado pelo abrigo.")
        void shouldNotCallExistsByPhoneWhenPhoneShelterIsEquals() {
            // Arrange
            Long shelterId = 1L;
            ShelterUpdateDto dtoUpdate = new ShelterUpdateDto("shelterupdateemail@email.com", shelter.getPhone());

            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));
            when(shelterRepository.save(any())).thenReturn(shelter);

            // Act
            shelterService.updateShelterById(shelterId, dtoUpdate);

            // Assert
            verify(shelterRepository, never()).existsByPhone(any());
            verify(shelterRepository).save(any());
        }

        @Test
        @DisplayName("Deve lançar uma DuplicationExistsException quando o telefone do abrigo já está em uso.")
        void shouldThrowDuplicationExistsExceptionWhenShelterPhoneIsAlreadyUsed() {
            // Arrange
            Long shelterId = 1L;
            ShelterUpdateDto dtoUpdate = new ShelterUpdateDto("shelterupdateemail@email.com", "1198765432");

            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));
            when(shelterRepository.existsByPhone(dtoUpdate.phone())).thenReturn(true);

            // Act
            assertThrows(DuplicationExistsException.class, () -> shelterService.updateShelterById(shelterId, dtoUpdate));
        }

        @Test
        @DisplayName("Não deve lançar uma DuplicationExistsException quando o telefone do abrigo ainda não está em uso.")
        void shouldNotThrowDuplicationExistsExceptionWhenShelterPhoneIsNotUsed() {
            // Arrange
            Long shelterId = 1L;
            ShelterUpdateDto dtoUpdate = new ShelterUpdateDto("shelterupdateemail@email.com", "1198765432");

            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));
            when(shelterRepository.existsByPhone(dtoUpdate.phone())).thenReturn(false);
            when(shelterRepository.save(any())).thenReturn(shelter);

            // Act
            assertDoesNotThrow(() -> shelterService.updateShelterById(shelterId, dtoUpdate));
        }

        @Test
        @DisplayName("Deve atualizar um abrigo com somente o email enviado.")
        void shouldUpdateAShelterWithOnlyEmailSend() {
            // Arrange
            Long shelterId = 1L;
            ShelterUpdateDto dtoUpdate = new ShelterUpdateDto("shelterupdateemail@email.com", null);

            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));
            when(shelterRepository.existsByEmail(dtoUpdate.email())).thenReturn(false);
            when(shelterRepository.save(shelter)).thenAnswer(i -> i.getArgument(0));

            // Act
            ShelterResponseDto result = shelterService.updateShelterById(shelterId, dtoUpdate);

            // Assert
            verify(shelterRepository).findById(shelterId);
            verify(shelterRepository).existsByEmail(dtoUpdate.email());
            verify(shelterRepository, never()).existsByPhone(any());
            verify(shelterRepository).save(shelter);

            assertEquals(dtoUpdate.email(), shelter.getEmail());

            assertNotNull(result);
            assertEquals(dtoUpdate.email(), result.email());
        }

        @Test
        @DisplayName("Deve atualizar um abrigo com somente o telefone enviado.")
        void shouldUpdateAShelterWithOnlyPhoneSend() {
            // Arrange
            Long shelterId = 1L;
            ShelterUpdateDto dtoUpdate = new ShelterUpdateDto(null, "1176543212");

            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));
            when(shelterRepository.existsByPhone(dtoUpdate.phone())).thenReturn(false);
            when(shelterRepository.save(shelter)).thenAnswer(i -> i.getArgument(0));

            // Act
            ShelterResponseDto result = shelterService.updateShelterById(shelterId, dtoUpdate);

            // Assert
            verify(shelterRepository).findById(shelterId);
            verify(shelterRepository).existsByPhone(dtoUpdate.phone());
            verify(shelterRepository, never()).existsByEmail(any());
            verify(shelterRepository).save(shelter);

            assertEquals(dtoUpdate.phone(), shelter.getPhone());

            assertNotNull(result);
            assertEquals(dtoUpdate.phone(), result.phone());
        }

        @Test
        @DisplayName("Deve atualizar um abrigo com email e telefone enviados.")
        void shouldUpdateAShelterWithEmailAndPhoneSends() {
            // Arrange
            Long shelterId = 1L;
            ShelterUpdateDto dtoUpdate = new ShelterUpdateDto("shelterupdateemail@email.com", "1176543212");

            when(shelterRepository.findById(shelterId)).thenReturn(Optional.of(shelter));
            when(shelterRepository.existsByEmail(dtoUpdate.email())).thenReturn(false);
            when(shelterRepository.existsByPhone(dtoUpdate.phone())).thenReturn(false);
            when(shelterRepository.save(shelter)).thenAnswer(i -> i.getArgument(0));

            // Act
            ShelterResponseDto result = shelterService.updateShelterById(shelterId, dtoUpdate);

            // Assert
            verify(shelterRepository).findById(shelterId);
            verify(shelterRepository).existsByEmail(dtoUpdate.email());
            verify(shelterRepository).existsByPhone(dtoUpdate.phone());
            verify(shelterRepository).save(shelter);

            assertEquals(dtoUpdate.email(), shelter.getEmail());
            assertEquals(dtoUpdate.phone(), shelter.getPhone());

            assertNotNull(result);
            assertEquals(dtoUpdate.email(), result.email());
            assertEquals(dtoUpdate.phone(), result.phone());
        }
    }
}