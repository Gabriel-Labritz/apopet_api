package br.com.gabriel_labritz.adopet.controllers;

import br.com.gabriel_labritz.adopet.dto.pets.PetRequestDto;
import br.com.gabriel_labritz.adopet.dto.pets.PetResponseDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterResponseDto;
import br.com.gabriel_labritz.adopet.enums.TypePet;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.services.PetService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class PetControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PetService petService;

    @Autowired
    private JacksonTester<PetRequestDto> petRequestDtoTester;

    @Nested
    class registerPet {
        @Test
        @DisplayName("Deve criar um pet com sucesso e retornar status 201.")
        void shouldCreateAPetWithSuccessAndReturn201Status() throws Exception {
            // Arrange
            ShelterResponseDto shelter = new ShelterResponseDto(1L, "shelter1@gmail.com", "1155555555");
            PetRequestDto request = new PetRequestDto("Sophia", "Gato", "Siâmes", 15, 5.5, "Marrom", 2L);
            PetResponseDto response = new PetResponseDto(
                    2L,
                    request.name(),
                    TypePet.toPetType(request.type()),
                    request.breed(), request.age(),
                    request.weight(),
                    request.color(), shelter);

            when(petService.registerPet(any())).thenReturn(response);

            // Act + Assert
            mvc.perform(post("/pets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(petRequestDtoTester.write(request).getJson()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(response.id()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.type").value(response.type().name()))
                    .andExpect(jsonPath("$.breed").value(response.breed()))
                    .andExpect(jsonPath("$.age").value(response.age()))
                    .andExpect(jsonPath("$.weight").value(response.weight()))
                    .andExpect(jsonPath("$.color").value(response.color()))
                    .andExpect(jsonPath("$.shelter.id").value(response.shelter().id()))
                    .andExpect(jsonPath("$.shelter.email").value(response.shelter().email()))
                    .andExpect(jsonPath("$.shelter.phone").value(response.shelter().phone()));

            // Assert
            verify(petService).registerPet(any(PetRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar status 400 quando os dados são inválidos.")
        void shouldReturn400StatusWhenInvalidDatas() throws Exception {
            // Arrange
            PetRequestDto request = new PetRequestDto("", "", "Siâmes", 15, 5.5, "Marrom", 2L);

            // Act + Assert
            mvc.perform(post("/pets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(petRequestDtoTester.write(request).getJson()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar status 404 quando o abrigo não for encontrado.")
        void shouldReturn404StatusWhenShelterNotFound() throws Exception {
            // Arrange
            PetRequestDto request = new PetRequestDto("Sophia", "Gato", "Siâmes", 15, 5.5, "Marrom", 2L);

            when(petService.registerPet(request)).thenThrow(new NotFoundException(ErrosMessages.SHELTER_NOTFOUND.getErrorMessage()));

            // Act + Assert
            mvc.perform(post("/pets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(petRequestDtoTester.write(request).getJson()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail").value("O abrigo não foi encontrado."))
                    .andExpect(jsonPath("$.instance").value("/pets"))
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.title").value("Not Found"));;

        }
    }

    @Nested
    class getAll {
        @Test
        @DisplayName("Deve retornar status 200.")
        void shouldReturn200Status() throws Exception {
            // Arrange
            ShelterResponseDto shelter = new ShelterResponseDto(1L, "shelter1@gmail.com", "1155555555");
            PetResponseDto response = new PetResponseDto(
                    2L,
                    "Sophia",
                    TypePet.toPetType("Gato"),
                    "Siâmes", 15,
                    5.1,
                    "Marrom", shelter);

            when(petService.getAllPets()).thenReturn(List.of(response));

            // Act + Assert
            mvc.perform(get("/pets"))
                    .andExpect(status().isOk());

            // Assert
            verify(petService).getAllPets();
        }
    }
}