package br.com.gabriel_labritz.adopet.controllers;

import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionRequestDto;
import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionResponseDto;
import br.com.gabriel_labritz.adopet.enums.AdoptionStatus;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.services.AdoptionService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class AdoptionControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AdoptionService adoptionService;

    @Autowired
    private JacksonTester<AdoptionRequestDto> adoptionRequestDtoTester;

    @Nested
    class adopte {
        @Test
        @DisplayName("Deve retornar 201 status quando solicitar uma adoção com sucesso.")
        void shouldReturn201StatusWhenSuccessRequestAdoption() throws Exception {
            // Arrange
            AdoptionRequestDto request = new AdoptionRequestDto(10L, 2L, "Motivo da adoção...");
            AdoptionResponseDto response = new AdoptionResponseDto(1L, LocalDate.now(), request.reason(), AdoptionStatus.EM_ANDAMENTO);

            when(adoptionService.adopetPet(request)).thenReturn(response);

            // Act + Assert
            mvc.perform(post("/adoption/adopet")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(adoptionRequestDtoTester.write(request).getJson()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.date").value(response.date().toString()))
                    .andExpect(jsonPath("$.reason").value(response.reason()))
                    .andExpect(jsonPath("$.status").value(response.status().name()));

            verify(adoptionService).adopetPet(any(AdoptionRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar 400 status quando os dados da solicitação são inválidos.")
        void shouldReturn400StatusWhenSolicitationDataIsInvalid() throws Exception {
            // Arrange
            AdoptionRequestDto request = new AdoptionRequestDto(-10L, -2L, "");

            // Act + Assert
            mvc.perform(post("/adoption/adopet")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(adoptionRequestDtoTester.write(request).getJson()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 404 status quando o tutor não for encontrado.")
        void shouldReturn404StatusWhenTutorNotFound() throws Exception {
            // Arrange
            AdoptionRequestDto request = new AdoptionRequestDto(10L, 2L, "Motivo da adoção...");

            when(adoptionService.adopetPet(request)).thenThrow(new NotFoundException(ErrosMessages.TUTOR_NOTFOUND.getErrorMessage()));

            // Act + Assert
            mvc.perform(post("/adoption/adopet")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(adoptionRequestDtoTester.write(request).getJson()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail").value("O Tutor não foi encontrado."))
                    .andExpect(jsonPath("$.instance").value("/adoption/adopet"))
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.title").value("Not Found"));
        }

        @Test
        @DisplayName("Deve retornar 404 status quando o pet não for encontrado.")
        void shouldReturn404StatusWhenPetNotFound() throws Exception {
            // Arrange
            AdoptionRequestDto request = new AdoptionRequestDto(10L, 2L, "Motivo da adoção...");

            when(adoptionService.adopetPet(request)).thenThrow(new NotFoundException(ErrosMessages.PET_NOTFOUND.getErrorMessage()));

            // Act + Assert
            mvc.perform(post("/adoption/adopet")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(adoptionRequestDtoTester.write(request).getJson()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail").value("O pet não foi encontrado."))
                    .andExpect(jsonPath("$.instance").value("/adoption/adopet"))
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.title").value("Not Found"));
        }

        @Test
        @DisplayName("Deve retornar 409 status quando alguma regra de negócio for violada.")
        void shouldReturn409StatusWhenViolateSomeBusinessRule() throws Exception {
            // Arrange
            AdoptionRequestDto request = new AdoptionRequestDto(10L, 2L, "Motivo da adoção...");

            when(adoptionService.adopetPet(request)).thenThrow(new AdoptionBusinessException(ErrosMessages.PET_ADOPTED.getErrorMessage()));

            // Act + Assert
            mvc.perform(post("/adoption/adopet")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(adoptionRequestDtoTester.write(request).getJson()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.detail").value("O pet já foi adotado."))
                    .andExpect(jsonPath("$.instance").value("/adoption/adopet"))
                    .andExpect(jsonPath("$.status").value("409"))
                    .andExpect(jsonPath("$.title").value("Conflict"));
        }
    }

    @Nested
    class getAdoptions {
        @Test
        @DisplayName("Deve retornar 200 status")
        void shouldReturn200Status() throws Exception {
            // Arrange
            AdoptionResponseDto response = new AdoptionResponseDto(1L, LocalDate.now(), "Motivo...", AdoptionStatus.EM_ANDAMENTO);

            when(adoptionService.getAllAdoptions()).thenReturn(List.of(response));

            // Act + Assert
            mvc.perform(get("/adoption"))
                    .andExpect(status().isOk());

            verify(adoptionService).getAllAdoptions();
        }
    }

    @Nested
    class getAdoption {
        @Test
        @DisplayName("Deve retornar 200 status quando a solicitação de adoção for encontrada.")
        void shouldReturn200StatusWhenAdoptionSolicitationIsFounded() throws Exception {
            // Arrange
            Long adoptionId = 1L;
            AdoptionResponseDto response = new AdoptionResponseDto(1L, LocalDate.now(), "Motivo...", AdoptionStatus.EM_ANDAMENTO);

            when(adoptionService.getAdoptionById(adoptionId)).thenReturn(response);

            // Act + Assert
            mvc.perform(get("/adoption/{id}", adoptionId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.date").value(response.date().toString()))
                    .andExpect(jsonPath("$.reason").value(response.reason()))
                    .andExpect(jsonPath("$.status").value(response.status().name()));

            verify(adoptionService).getAdoptionById(adoptionId);
        }

        @Test
        @DisplayName("Deve retornar 400 status quando o id da solicitação de adoção é inválido.")
        void shouldReturn400StatusWhenAdoptionSolicitationIdIsInvalid() throws Exception {
            // Arrange
            Long adoptionId = -1L;

            // Act + Assert
            mvc.perform(get("/adoption/{id}", adoptionId))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 404 status quando a solicitação de adoção não for encontrada.")
        void shouldReturn404StatusWhenAdoptionSolicitationNotFound() throws Exception {
            // Arrange
            Long adoptionId = 1L;

            when(adoptionService.getAdoptionById(adoptionId)).thenThrow(new NotFoundException(ErrosMessages.ADOPTION_NOTFOUND.getErrorMessage()));

            // Act + Assert
            mvc.perform(get("/adoption/{id}", adoptionId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail").value("A adoção não foi encontrada."))
                    .andExpect(jsonPath("$.instance").value("/adoption/1"))
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.title").value("Not Found"));;
        }
    }
}