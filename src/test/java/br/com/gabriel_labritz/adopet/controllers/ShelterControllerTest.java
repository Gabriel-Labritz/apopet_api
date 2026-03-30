package br.com.gabriel_labritz.adopet.controllers;

import br.com.gabriel_labritz.adopet.dto.shelter.ShelterRequestDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterResponseDto;
import br.com.gabriel_labritz.adopet.dto.tutor.TutorRequestDto;
import br.com.gabriel_labritz.adopet.dto.tutor.TutorResponseDto;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.DuplicationExistsException;
import br.com.gabriel_labritz.adopet.services.ShelterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ShelterControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ShelterService shelterService;

    @Autowired
    private JacksonTester<ShelterRequestDto> shelterRequestDtoTester;

    @Nested
    class registerShelter {
        @Test
        @DisplayName("Deve criar um abrigo com sucesso e retornar status 201.")
        void shouldCreateAShelterWithSuccessAndReturn201Status() throws Exception {
            // Arrange
            ShelterRequestDto request = new ShelterRequestDto("shelter1@gmail.com", "1199999999");
            ShelterResponseDto response = new ShelterResponseDto(1L, request.email(), request.phone());

            when(shelterService.shelterRegister(request)).thenReturn(response);

            // Act + Assert
            mvc.perform(post("/shelter")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shelterRequestDtoTester.write(request).getJson()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.email").value(response.email()))
                    .andExpect(jsonPath("$.phone").value(response.phone()));

            // Assert
            verify(shelterService).shelterRegister(any(ShelterRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar status 400 quando os dados são inválidos.")
        void shouldReturn400StatusWhenInvalidDatas() throws Exception {
            // Arrange
            ShelterRequestDto request = new ShelterRequestDto("", "");

            // Act + Assert
            mvc.perform(post("/shelter")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(shelterRequestDtoTester.write(request).getJson())).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar status 409 quando o email ou telefone já estão em uso.")
        void shouldReturn409StatusWhenEmailShelterOrPhoneShelterAlreadyUsed() throws Exception {
            // Arrange
            ShelterRequestDto request = new ShelterRequestDto("shelter1@gmail.com", "1199999999");

            when(shelterService.shelterRegister(request)).thenThrow(new DuplicationExistsException(ErrosMessages.EMAIL_EXISTS.getErrorMessage()));

            // Act + Assert
            mvc.perform(post("/shelter")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shelterRequestDtoTester.write(request).getJson()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.detail").value("O e-mail informado já está em uso."))
                    .andExpect(jsonPath("$.instance").value("/shelter"))
                    .andExpect(jsonPath("$.status").value("409"))
                    .andExpect(jsonPath("$.title").value("Conflict"));

            // Assert
            verify(shelterService).shelterRegister(any(ShelterRequestDto.class));
        }
    }
}