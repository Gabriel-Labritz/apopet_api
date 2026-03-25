package br.com.gabriel_labritz.adopet.controllers;

import br.com.gabriel_labritz.adopet.dto.error.ErrorSchema;
import br.com.gabriel_labritz.adopet.dto.error.ErrorValidationSchema;
import br.com.gabriel_labritz.adopet.dto.pets.PetRequestDto;
import br.com.gabriel_labritz.adopet.dto.pets.PetResponseDto;
import br.com.gabriel_labritz.adopet.dto.pets.UpdatePetDto;
import br.com.gabriel_labritz.adopet.services.PetService;
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

import java.util.List;

@Tag(name = "Pet")
@RestController
@RequestMapping("/pets")
public class PetController {
    @Autowired
    private PetService petService;

    @Operation(summary = "Criar um pet", description = "Esse endpoint cria um novo pet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pet foi criado com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PetResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "404", description = "Abrigo não encontrado.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            ))
    })
    @PostMapping
    public ResponseEntity<PetResponseDto> registerPet(@RequestBody @Valid PetRequestDto petRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.petService.registerPet(petRequestDto));
    }

    @Operation(summary = "Busca todos os pets cadastrados",
            description = "Esse endpoint busca a lista de pets cadastrados na base de dados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pets foi retornada com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PetResponseDto[].class)
            ))
    })
    @GetMapping
    public ResponseEntity<List<PetResponseDto>> getAll() {
        return ResponseEntity.ok().body(this.petService.getAllPets());
    }

    @Operation(summary = "Busca todos os pets disponíveis",
            description = "Esse endpoint busca a lista de pets disponíveis para adoção."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pets foi retornada com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PetResponseDto[].class)
            ))
    })
    @GetMapping("/avaliable")
    public ResponseEntity<List<PetResponseDto>> getAllAvaliable() {
        return ResponseEntity.ok().body(this.petService.getAllPetsAvailable());
    }

    @Operation(summary = "Busca pet por id",
            description = "Esse endpoint busca um pet pelo parâmetro id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet foi encontrado com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PetResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Id inválido", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "404", description = "O pet não foi encontrado.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            ))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDto> getPet(@PathVariable @Positive Long id) {
        return ResponseEntity.ok().body(this.petService.getPetById(id));
    }

    @Operation(summary = "Atualiza pet por id",
            description = "Esse endpoint busca pet pelo parâmetro id informado e atualiza as informações."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet foi atualizado com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PetResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "404", description = "O pet e/ou abrigo não foi encontrado.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            )),
            @ApiResponse(responseCode = "409", description = "O pet já foi adotado.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            ))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<PetResponseDto> updatePet(@PathVariable @Positive Long id, @RequestBody @Valid UpdatePetDto updatePetDto) {
        return ResponseEntity.ok().body(this.petService.updatePetById(id, updatePetDto));
    }
}
