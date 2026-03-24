package br.com.gabriel_labritz.adopet.services;

import br.com.gabriel_labritz.adopet.dto.tutor.TutorRequestDto;
import br.com.gabriel_labritz.adopet.dto.tutor.TutorResponseDto;
import br.com.gabriel_labritz.adopet.dto.tutor.TutorUpdateDto;
import br.com.gabriel_labritz.adopet.exceptions.DuplicationExistsException;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Tutor;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {
    @InjectMocks
    private TutorService tutorService;

    @Mock
    private TutorRepository tutorRepository;

    private Tutor tutor;

    @Captor
    private ArgumentCaptor<Tutor> tutorCaptor;

    private TutorRequestDto tutorRequestDto;

    private TutorResponseDto tutorResponseDto;

    @BeforeEach
    void setUp() {
        this.tutorRequestDto = new TutorRequestDto("Teste", "teste@teste.com", "1198888888");
        this.tutor = new Tutor(tutorRequestDto);
        this.tutorResponseDto = new TutorResponseDto("Teste", "teste@teste.com", "1198888888");
    }

    @Nested
    class register {
        @Test
        @DisplayName("Deve chamar existsByEmail.")
        void shouldCallExistsByEmail() {
            // Arrange
            when(tutorRepository.existsByEmail(tutorRequestDto.email())).thenReturn(false);
            when(tutorRepository.save(any())).thenReturn(tutor);

            // Act
            tutorService.register(tutorRequestDto);

            //Assert
            verify(tutorRepository).existsByEmail(tutorRequestDto.email());
        }

        @Test
        @DisplayName("Deve lançar uma DuplicationExistsException quando o email informado do tutor já está em uso.")
        void shouldThrowDuplicationExistsExceptionWhenTutorEmailAlreadyUsed() {
            // Arrange
            when(tutorRepository.existsByEmail(tutorRequestDto.email())).thenReturn(true);

            // Act + Assert
            assertThrows(DuplicationExistsException.class, () -> tutorService.register(tutorRequestDto));
            verify(tutorRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve chamar tutorRepository.save com os valores corretos.")
        void shouldCallTutorRepositorySaveWithCorrectValues() {
            // Arrange
            when(tutorRepository.existsByEmail(tutorRequestDto.email())).thenReturn(false);
            when(tutorRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // Act
            tutorService.register(tutorRequestDto);

            // Assert
            verify(tutorRepository).save(tutorCaptor.capture());
            Tutor tutorCapture = tutorCaptor.getValue();

            assertEquals(tutorRequestDto.name(), tutorCapture.getName());
            assertEquals(tutorRequestDto.email(), tutorCapture.getEmail());
            assertEquals(tutorRequestDto.phone(), tutorCapture.getPhone());
        }

        @Test
        @DisplayName("Deve retornar um tutorResponseDto corretamente.")
        void shouldReturnTutorResponseDtoCorrectly() {
            // Arrange
            when(tutorRepository.existsByEmail(tutorRequestDto.email())).thenReturn(false);
            when(tutorRepository.save(any())).thenReturn(tutor);

            // Act
            TutorResponseDto result = tutorService.register(tutorRequestDto);

            // Assert
            assertEquals(result, tutorResponseDto);
        }
    }

    @Nested
    class findTutorEntityById {
        @Test
        @DisplayName("Deve lançar uma NotFoundException quando o tutor não for encontrado.")
        void shouldThrowNotFoundExceptionWhenTutorNotFound() {
            // Arrange
            Long tutorId = 2L;
            when(tutorRepository.findById(tutorId)).thenReturn(Optional.empty());

            // Act + Assert
            assertThrows(NotFoundException.class, () -> tutorService.findTutorEntityById(tutorId));
        }

        @Test
        @DisplayName("Deve retornar com sucesso um tutor pelo id")
        void shouldReturnATutorByIdParameter() {
            // Arrange
            Long tutorId = 2L;
            when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));

            // Act
            Tutor result = tutorService.findTutorEntityById(tutorId);

            // Assert
            verify(tutorRepository).findById(tutorId);
            assertEquals(tutor, result);
        }
    }

    @Nested
    class getTutorById {
        @Test
        @DisplayName("Deve retornar com sucesso um tutor pelo id")
        void shouldReturnATutorById() {
            // Arrange
            Long tutorId = 2L;
            when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));

            // Act
            TutorResponseDto result = tutorService.getTutorById(tutorId);

            // Assert
            verify(tutorRepository).findById(tutorId);
            assertNotNull(result);
            assertEquals(tutorResponseDto, result);
        }
    }

    @Nested
    class updateTutorById {
        @Test
        @DisplayName("Não deve chamar existsByEmail quando o email não é enviado.")
        void shouldNotCallExistsByEmailWhenEmailIsNotSend() {
            // Arrange
            Long tutorId = 2L;
            TutorUpdateDto dtoUpdate = new TutorUpdateDto(null, "11988888888");

            when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));
            when(tutorRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // Act
            tutorService.updateTutorById(tutorId, dtoUpdate);

            // Assert
            verify(tutorRepository, never()).existsByEmail(any());
            verify(tutorRepository).save(any());
        }

        @Test
        @DisplayName("Não deve chamar existsByEmail quando o email que será atualizado é o mesmo que já está sendo usado pelo tutor.")
        void shouldNotCallExistsByEmailWhenEmailIsEquals() {
            // Arrange
            Long tutorId = 2L;
            TutorUpdateDto dtoUpdate = new TutorUpdateDto(tutor.getEmail(), "11988888888");

            when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));
            when(tutorRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // Act
            tutorService.updateTutorById(tutorId, dtoUpdate);

            // Assert
            verify(tutorRepository, never()).existsByEmail(any());
            verify(tutorRepository).save(any());
        }

        @Test
        @DisplayName("Deve lançar uma DuplicationExistsException quando o email informado para a atualização já está em uso.")
        void shouldThrowDuplicationExistsExceptionWhenEmailAlreadyUsed() {
            // Arrange
            Long tutorId = 2L;
            TutorUpdateDto dtoUpdate = new TutorUpdateDto("emailupdate@test.com", "11988888888");

            when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));
            when(tutorRepository.existsByEmail(dtoUpdate.email())).thenReturn(true);

            // Act + Assert
            assertThrows(DuplicationExistsException.class, () -> tutorService.updateTutorById(tutorId, dtoUpdate));
            verify(tutorRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve atualizar os dados do tutor com sucesso.")
        void shouldUpdateTutorDataWithSuccess() {
            // Arrange
            Long tutorId = 2L;
            TutorUpdateDto dtoUpdate = new TutorUpdateDto("emailupdate@test.com", "11988888888");

            when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));
            when(tutorRepository.existsByEmail(dtoUpdate.email())).thenReturn(false);
            when(tutorRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // Act
            TutorResponseDto result = tutorService.updateTutorById(tutorId, dtoUpdate);

            // Assert
            verify(tutorRepository).findById(tutorId);
            verify(tutorRepository).existsByEmail(dtoUpdate.email());
            verify(tutorRepository).save(tutor);

            assertEquals(dtoUpdate.email(), tutor.getEmail());
            assertEquals(dtoUpdate.phone(), tutor.getPhone());

            assertNotNull(result);
            assertEquals(dtoUpdate.email(), result.email());
            assertEquals(dtoUpdate.phone(), result.phone());
        }
    }
}