package br.com.gabriel_labritz.adopet.controllers;

import br.com.gabriel_labritz.adopet.dto.tutor.TutorRequestDto;
import br.com.gabriel_labritz.adopet.dto.tutor.TutorResponseDto;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.DuplicationExistsException;
import br.com.gabriel_labritz.adopet.services.TutorService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class TutorControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TutorService tutorService;

    @Autowired
    JacksonTester<TutorRequestDto> tutorRequestDtoTester;

    @Nested
    class registerTutor {
        @Test
        @DisplayName("Deve criar um tutor com sucesso e retornar status 201.")
        void shouldCreateATutorWithSuccessAndReturn201Status() throws Exception {
            // Arrange
            TutorRequestDto request = new TutorRequestDto("Jonh", "jonh@gmail.com", "11987654321");
            TutorResponseDto response = new TutorResponseDto("Jonh", "jonh@gmail.com", "11987654321");

            when(tutorService.register(any())).thenReturn(response);

            // Act + Assert
            mvc.perform(post("/tutor")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tutorRequestDtoTester.write(request).getJson()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.email").value(response.email()))
                    .andExpect(jsonPath("$.phone").value(response.phone()));

            // Assert
            verify(tutorService).register(any(TutorRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar status 400 quando os dados enviados são inválidos.")
        void shouldReturn400StatusWhenInvalidData() throws Exception {
            // Arrange
            TutorRequestDto request = new TutorRequestDto("", "", "");

            // Act + Assert
            mvc.perform(post("/tutor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(tutorRequestDtoTester.write(request).getJson())).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar status 409 quando o email já está em uso.")
        void shouldReturn409StatusWhenEmailAlreadyUsed() throws Exception {
            // Arrange
            TutorRequestDto request = new TutorRequestDto("Jonh", "jonh@gmail.com", "11987654321");

            when(tutorService.register(any())).thenThrow(new DuplicationExistsException(ErrosMessages.EMAIL_EXISTS.getErrorMessage()));

            // Act + Assert
            mvc.perform(post("/tutor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(tutorRequestDtoTester.write(request).getJson()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.detail").value("O e-mail informado já está em uso."))
                    .andExpect(jsonPath("$.instance").value("/tutor"))
                    .andExpect(jsonPath("$.status").value("409"))
                    .andExpect(jsonPath("$.title").value("Conflict"));

            // Assert
            verify(tutorService).register(any(TutorRequestDto.class));
        }
    }
}