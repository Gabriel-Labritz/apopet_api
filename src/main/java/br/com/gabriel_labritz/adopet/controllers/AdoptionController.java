package br.com.gabriel_labritz.adopet.controllers;

import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionRequestDto;
import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionResponseDto;
import br.com.gabriel_labritz.adopet.dto.error.ErrorSchema;
import br.com.gabriel_labritz.adopet.dto.error.ErrorValidationSchema;
import br.com.gabriel_labritz.adopet.services.AdoptionService;
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

@Tag(name = "Adoption")
@RestController
@RequestMapping("/adoption")
public class AdoptionController {
    @Autowired
    private AdoptionService adoptionService;

    @Operation(summary = "Solicita uma adoção", description = "Esse endpoint solicita uma nova adoção.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitação de adoção foi feita com sucesso.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AdoptionResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "404", description = "O Tutor e/ou pet não foi encontrado.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            )),
            @ApiResponse(responseCode = "409", description = "Regras de negócio violada.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorSchema.class)
            ))
    })
    @PostMapping("/adopet")
    public ResponseEntity<AdoptionResponseDto> adopte(@RequestBody @Valid AdoptionRequestDto adoptionRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.adoptionService.adopetPet(adoptionRequestDto));
    }

    @Operation(summary = "Busca todas as solicitações de adoções", description = "Esse endpoint busca todas as adoções já solicitadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitações de adoções foram retornadas com sucesso.", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = AdoptionResponseDto[].class)
            ))
    })
    @GetMapping
    public ResponseEntity<List<AdoptionResponseDto>> getAdoptions() {
        return ResponseEntity.ok().body(this.adoptionService.getAllAdoptions());
    }

    @Operation(summary = "Busca uma solicitação de adoções", description = "Esse endpoint busca uma solicitação de adoção pelo parâmetro id informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A solicitação de adoção foi encontrada com sucesso.", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = AdoptionResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Id inválido.", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "404", description = "Solicitação de adoção não foi encontrada.", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorSchema.class)
            ))
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdoptionResponseDto> getAdoption(@PathVariable @Positive Long id) {
        return ResponseEntity.ok().body(this.adoptionService.getAdoptionById(id));
    }

    @Operation(
            summary = "Aprova uma solicitação de adoção",
            description = "Esse endpoint aprova uma solicitação de adoção pelo parâmetro id informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "A solicitação de adoção foi encontrada e aprovada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Id inválido.", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "404", description = "Solicitação de adoção não foi encontrada.", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorSchema.class)
            )),
            @ApiResponse(responseCode = "409", description = "Essa solicitação já foi aprovada/reprovada.", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorSchema.class)
            ))
    })
    @PutMapping("/{id}/approve")
    public ResponseEntity approve(@PathVariable @Positive Long id) {
        this.adoptionService.approveAdoption(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Reprova uma solicitação de adoção",
            description = "Esse endpoint reprova uma solicitação de adoção pelo parâmetro id informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "A solicitação de adoção foi encontrada e reprovada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Id inválido.", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorValidationSchema.class)
            )),
            @ApiResponse(responseCode = "404", description = "Solicitação de adoção não foi encontrada.", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorSchema.class)
            )),
            @ApiResponse(responseCode = "409", description = "Essa solicitação já foi aprovada/reprovada.", content = @Content(
                    mediaType = "application/json", schema = @Schema(implementation = ErrorSchema.class)
            ))
    })
    @PutMapping("/{id}/disapprove")
    public ResponseEntity disapprove(@PathVariable @Positive Long id) {
        this.adoptionService.disapproveAdoption(id);
        return ResponseEntity.noContent().build();
    }
}
