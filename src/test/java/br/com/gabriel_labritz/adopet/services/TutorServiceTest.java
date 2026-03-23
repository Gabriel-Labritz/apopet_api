package br.com.gabriel_labritz.adopet.services;

import br.com.gabriel_labritz.adopet.dto.tutor.TutorRequestDto;
import br.com.gabriel_labritz.adopet.dto.tutor.TutorResponseDto;
import br.com.gabriel_labritz.adopet.exceptions.DuplicationExistsException;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Tutor;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.TutorRepository;
import org.junit.jupiter.api.*;
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

    @Captor
    private ArgumentCaptor<Long> tutorIdCaptor;

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
            when(tutorRepository.save(any())).thenReturn(tutor);

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
}