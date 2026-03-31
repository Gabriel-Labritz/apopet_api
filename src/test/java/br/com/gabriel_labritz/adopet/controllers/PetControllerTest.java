package br.com.gabriel_labritz.adopet.controllers;

import br.com.gabriel_labritz.adopet.dto.pets.PetRequestDto;
import br.com.gabriel_labritz.adopet.dto.pets.PetResponseDto;
import br.com.gabriel_labritz.adopet.dto.pets.UpdatePetDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterResponseDto;
import br.com.gabriel_labritz.adopet.enums.TypePet;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
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

import java.io.IOException;
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

    @Autowired
    private JacksonTester<UpdatePetDto> petUpdateDtoTester;

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
                    .andExpect(jsonPath("$.title").value("Not Found"));

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

    @Nested
    class getAllAvaliable {
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

            when(petService.getAllPetsAvailable()).thenReturn(List.of(response));

            // Act + Assert
            mvc.perform(get("/pets/avaliable"))
                    .andExpect(status().isOk());

            // Assert
            verify(petService).getAllPetsAvailable();
        }
    }

    @Nested
    class getPet {
        @Test
        @DisplayName("Deve retornar status 200 quando o pet for encontrado.")
        void shouldReturn200StatusWhenPetFounded() throws Exception {
            // Arrange
            Long petId = 2L;
            ShelterResponseDto shelter = new ShelterResponseDto(1L, "shelter1@gmail.com", "1155555555");
            PetResponseDto response = new PetResponseDto(
                    2L,
                    "Sophia",
                    TypePet.toPetType("Gato"),
                    "Siâmes", 15,
                    5.1,
                    "Marrom", shelter);

            when(petService.getPetById(petId)).thenReturn(response);

            // Act + Assert
            mvc.perform(get("/pets/{id}", petId))
                    .andExpect(status().isOk())
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
            verify(petService).getPetById(petId);
        }

        @Test
        @DisplayName("Deve retornar status 400 quando o id é inválido.")
        void shouldReturn400StatusWhenPetIdIsInvalid() throws Exception {
            // Arrange
            Long petId = -2L;

            // Act + Assert
            mvc.perform(get("/pets/{id}", petId))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar status 404 quando o pet não for encontrado.")
        void shouldReturn404StatusWhenPetNotFound() throws Exception {
            // Arrange
            Long petId = 2L;

            when(petService.getPetById(petId)).thenThrow(new NotFoundException(ErrosMessages.PET_NOTFOUND.getErrorMessage()));

            // Act + Assert
            mvc.perform(get("/pets/{id}", petId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail").value("O pet não foi encontrado."))
                    .andExpect(jsonPath("$.instance").value("/pets/2"))
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.title").value("Not Found"));
        }
    }

    @Nested
    class updatePet {
        @Test
        @DisplayName("Deve retornar status 200 quando o pet for atualizado.")
        void shouldReturn200StatusWhenPetIsUpdated() throws Exception {
            // Arrange
            Long petId = 2L;
            ShelterResponseDto shelter = new ShelterResponseDto(2L, "shelter2@gmail.com", "1188888888");
            UpdatePetDto request = new UpdatePetDto(13, 5.2, 2L);
            PetResponseDto response = new PetResponseDto(
                    2L,
                    "Sophia",
                    TypePet.GATO,
                    "Siâmes", request.age(),
                    request.weight(),
                    "Marrom", shelter);

            when(petService.updatePetById(petId, request)).thenReturn(response);

            // Act + Assert
            mvc.perform(patch("/pets/{id}", petId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(petUpdateDtoTester.write(request).getJson()))
                    .andExpect(status().isOk())
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
            verify(petService).updatePetById(petId, request);
        }

        @Test
        @DisplayName("Deve retornar status 400 quando o id pet é inválido.")
        void shouldReturn400StatusWhenPetIdIsInvalid() throws Exception {
            // Arrange
            Long petId = -2L;

            // Act + Assert
            mvc.perform(patch("/pets/{id}", petId))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar status 400 quando os dados de atualização do pet são inválidos.")
        void shouldReturn400StatusWhenUpdatePetDataIsInvalid() throws Exception {
            // Arrange
            Long petId = 2L;
            UpdatePetDto request = new UpdatePetDto(-13, -5.2, -2L);

            // Act + Assert
            mvc.perform(patch("/pets/{id}", petId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(petUpdateDtoTester.write(request).getJson()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar status 404 quando o pet não for encontrado.")
        void shouldReturn404StatusWhenPetNotFound() throws Exception {
            // Arrange
            Long petId = 2L;
            UpdatePetDto request = new UpdatePetDto(13, 5.2, 2L);

            when(petService.updatePetById(petId, request)).thenThrow(new NotFoundException(ErrosMessages.PET_NOTFOUND.getErrorMessage()));

            // Act + Assert
            mvc.perform(patch("/pets/{id}", petId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(petUpdateDtoTester.write(request).getJson()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail").value("O pet não foi encontrado."))
                    .andExpect(jsonPath("$.instance").value("/pets/2"))
                    .andExpect(jsonPath("$.status").value("404"))
                    .andExpect(jsonPath("$.title").value("Not Found"));
        }

        @Test
        @DisplayName("Deve retornar status 409 quando o pet que será atualizado já foi adotado.")
        void shouldReturn409StatusWhenPetAlreadyAdopted() throws Exception {
            // Arrange
            Long petId = 2L;
            UpdatePetDto request = new UpdatePetDto(13, 5.2, 2L);

            when(petService.updatePetById(petId, request)).thenThrow(new AdoptionBusinessException(ErrosMessages.UPDATE_PET_ADOPTED.getErrorMessage()));

            // Act + Assert
            mvc.perform(patch("/pets/{id}", petId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(petUpdateDtoTester.write(request).getJson()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.detail").value("Não é possível atualizar um pet já adotado."))
                    .andExpect(jsonPath("$.instance").value("/pets/2"))
                    .andExpect(jsonPath("$.status").value("409"))
                    .andExpect(jsonPath("$.title").value("Conflict"));
        }
    }
}