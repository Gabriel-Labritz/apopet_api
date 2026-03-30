package br.com.gabriel_labritz.adopet.controllers;

import br.com.gabriel_labritz.adopet.dto.shelter.ShelterRequestDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterResponseDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterUpdateDto;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.DuplicationExistsException;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private JacksonTester<ShelterUpdateDto> shelterUpdateDtoTester;

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
        }
    }

    @Nested
    class getShelter {
        @Test
        @DisplayName("Deve retornar status 200 quando o abrigo for encontrado.")
        void shouldReturn200StatusWhenShelterFound() throws Exception {
            // Arrange
            Long shelterId = 1L;
            ShelterResponseDto response = new ShelterResponseDto(1L, "shelter1@gmail.com", "1199999999");

            when(shelterService.getShelterById(shelterId)).thenReturn(response);

            // Act + Assert
            mvc.perform(get("/shelter/{id}", shelterId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.email").value(response.email()))
                    .andExpect(jsonPath("$.phone").value(response.phone()));
        }

        @Test
        @DisplayName("Deve retornar status 400 quando o id do abrigo for inválido.")
        void shouldReturn400StatusWhenShelterIdInvalid() throws Exception {
            // Arrange
            Long shelterId = -1L;

            // Act + Assert
            mvc.perform(get("/shelter/{id}", shelterId))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar status 404 quando o abrigo não for encontrado.")
        void shouldReturn404StatusWhenShelterNotFound() throws Exception {
            // Arrange
            Long shelterId = 1L;

            when(shelterService.getShelterById(shelterId)).thenThrow(new NotFoundException(ErrosMessages.SHELTER_NOTFOUND.getErrorMessage()));

            // Act + Assert
            mvc.perform(get("/shelter/{id}", shelterId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail").value("O abrigo não foi encontrado."))
                    .andExpect(jsonPath("$.instance").value("/shelter/1"))
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.title").value("Not Found"));
        }
    }

    @Nested
    class updateShelter {
        @Test
        @DisplayName("Deve retornar 200 status quando o abrigo for atualizado")
        void shouldReturn200StatusWhenShelterIsUpdated() throws Exception {
            // Arrange
            Long shelterId = 3L;
            ShelterUpdateDto request = new ShelterUpdateDto("shelterupdated@gmail.com", "1188888888");
            ShelterResponseDto response = new ShelterResponseDto(1L, request.email(), request.phone());

            when(shelterService.updateShelterById(shelterId, request)).thenReturn(response);

            // Act + Assert
            mvc.perform(patch("/shelter/{id}", shelterId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shelterUpdateDtoTester.write(request).getJson()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.email").value(response.email()))
                    .andExpect(jsonPath("$.phone").value(response.phone()));

            // Assert
            verify(shelterService).updateShelterById(shelterId, request);
        }

        @Test
        @DisplayName("Deve retornar status 400 quando o id do abrigo for inválido.")
        void shouldReturn400StatusWhenShelterIdInvalid() throws Exception {
            // Arrange
            Long shelterId = -3L;

            // Act + Assert
            mvc.perform(patch("/shelter/{id}", shelterId))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar status 400 quando os dados do abrigo forem inválidos.")
        void shouldReturn400StatusWhenInvalidData() throws Exception {
            // Arrange
            Long shelterId = 3L;
            ShelterUpdateDto request = new ShelterUpdateDto("shelter", "1188888889888111");

            // Act + Assert
            mvc.perform(patch("/shelter/{id}", shelterId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shelterUpdateDtoTester.write(request).getJson()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar status 404 quando o abrigo não for encontrado.")
        void shouldReturn404StatusWhenShelterNotFound() throws Exception {
            // Arrange
            Long shelterId = 3L;
            ShelterUpdateDto request = new ShelterUpdateDto("shelterupdated@gmail.com", "1188888888");

            when(shelterService.updateShelterById(shelterId, request)).thenThrow(new NotFoundException(ErrosMessages.SHELTER_NOTFOUND.getErrorMessage()));

            // Act + Assert
            mvc.perform(patch("/shelter/{id}", shelterId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shelterUpdateDtoTester.write(request).getJson()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail").value("O abrigo não foi encontrado."))
                    .andExpect(jsonPath("$.instance").value("/shelter/3"))
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.title").value("Not Found"));
        }

        @Test
        @DisplayName("Deve retornar status 409 quando o email e/ou telefone do abrigo já estão em uso.")
        void shouldReturn409StatusWhenShelterEmailOrPhoneAlreadyUsed() throws Exception {
            // Arrange
            Long shelterId = 3L;
            ShelterUpdateDto request = new ShelterUpdateDto("shelterupdated@gmail.com", "1188888888");

            when(shelterService.updateShelterById(shelterId, request)).thenThrow(new DuplicationExistsException(ErrosMessages.EMAIL_EXISTS.getErrorMessage()));

            // Act + Assert
            mvc.perform(patch("/shelter/{id}", shelterId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(shelterUpdateDtoTester.write(request).getJson()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.detail").value("O e-mail informado já está em uso."))
                    .andExpect(jsonPath("$.instance").value("/shelter/3"))
                    .andExpect(jsonPath("$.status").value("409"))
                    .andExpect(jsonPath("$.title").value("Conflict"));
        }
    }
}