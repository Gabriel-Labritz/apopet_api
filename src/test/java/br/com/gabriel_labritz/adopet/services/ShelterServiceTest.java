package br.com.gabriel_labritz.adopet.services;

import br.com.gabriel_labritz.adopet.dto.shelter.ShelterRequestDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterResponseDto;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
         this.shelterResponseDto = new ShelterResponseDto(shelter.getId(), "shelter@email.com", "1123456789");
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
}