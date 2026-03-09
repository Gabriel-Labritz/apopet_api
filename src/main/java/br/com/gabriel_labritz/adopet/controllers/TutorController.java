package br.com.gabriel_labritz.adopet.controllers;

import br.com.gabriel_labritz.adopet.dto.error.ErrorSchema;
import br.com.gabriel_labritz.adopet.dto.error.ErrorValidationSchema;
import br.com.gabriel_labritz.adopet.dto.tutor.TutorRequestDto;
import br.com.gabriel_labritz.adopet.dto.tutor.TutorResponseDto;
import br.com.gabriel_labritz.adopet.dto.tutor.TutorUpdateDto;
import br.com.gabriel_labritz.adopet.services.TutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tutor")
@RestController
@RequestMapping("/tutor")
public class TutorController {
    @Autowired
    private TutorService tutorService;

    @Operation(summary = "Criar tutor", description = "Esse endpoint cria um novo tutor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "O tutor foi criado com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TutorResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "409", description = "Email já está em uso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            ))
    })
    @PostMapping
    public ResponseEntity<TutorResponseDto> registerTutor(@RequestBody @Valid TutorRequestDto tutorRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tutorService.register(tutorRequestDto));
    }

    @Operation(summary = "Busca um tutor pelo id", description = "Esse endpoint busca um tutor pelo parâmetro de id informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "O tutor foi encontrado com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TutorResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "404", description = "Tutor não foi encontrado.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            )),
    })
    @GetMapping("/{id}")
    public ResponseEntity<TutorResponseDto> getTutor(@PathVariable @Positive Long id) {
        return ResponseEntity.ok().body(tutorService.getTutorById(id));
    }

    @Operation(
            summary = "Atualiza o tutor pelo id",
            description = "Esse endpoint busca um tutor pelo parâmetro de id informado e atualiza suas informações.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Os dados do tutor foram atualizados com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TutorResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "404", description = "Tutor não foi encontrado.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            )),
            @ApiResponse(responseCode = "409", description = "O email já está em uso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            )),
    })
    @PatchMapping("/{id}")
    public ResponseEntity<TutorResponseDto> updateTutor(
            @PathVariable @Positive Long id,
            @RequestBody @Valid TutorUpdateDto tutorUpdateDto) {
        return ResponseEntity.ok().body(tutorService.updateTutorById(id, tutorUpdateDto));
    }
}
