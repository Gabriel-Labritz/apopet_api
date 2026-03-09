package br.com.gabriel_labritz.adopet.controllers;

import br.com.gabriel_labritz.adopet.dto.error.ErrorSchema;
import br.com.gabriel_labritz.adopet.dto.error.ErrorValidationSchema;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterRequestDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterResponseDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterUpdateDto;
import br.com.gabriel_labritz.adopet.services.ShelterService;
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

@Tag(name = "Shelter")
@RestController
@RequestMapping("/shelter")
public class ShelterController {

    @Autowired
    private ShelterService shelterService;

    @Operation(summary = "Criar abrigo", description = "Esse endpoint criar um novo abrigo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "O abrigo foi criado com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ShelterResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "409", description = "O email e/ou telefone já está em uso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            ))
    })
    @PostMapping
    public ResponseEntity<ShelterResponseDto> registerShelter(@RequestBody @Valid ShelterRequestDto shelterRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shelterService.shelterRegister(shelterRequestDto));
    }

    @Operation(summary = "Busca abrigo por id", description = "Esse endpoint busca abrigo pelo parâmetro id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "O abrigo foi encontrado com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ShelterResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Id inválido", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "404", description = "O abrigo não foi encontrado", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            ))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ShelterResponseDto> getShelter(@PathVariable @Positive Long id) {
        return ResponseEntity.ok().body(shelterService.getShelterById(id));
    }

    @Operation(
            summary = "Atualiza abrigo por id",
            description = "Esse endpoint busca um abrigo pelo parâmetro de id informado e atualiza suas informações."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "O abrigo foi atualizado com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ShelterResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "404", description = "O abrigo não foi encontrado", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            )),
            @ApiResponse(responseCode = "409", description = "O email e/ou telefone já está em uso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            ))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ShelterResponseDto> updateShelter(
            @PathVariable @Positive Long id,
            @RequestBody @Valid ShelterUpdateDto shelterUpdateDto) {
        return ResponseEntity.ok().body(shelterService.updateShelterById(id, shelterUpdateDto));
    }
}
