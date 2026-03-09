package br.com.gabriel_labritz.adopet.dto.adoption;

import br.com.gabriel_labritz.adopet.enums.AdoptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record AdoptionResponseDto(
        @Schema(example = "2")
        Long id,
        LocalDate date,
        @Schema(example = "Motivo da adoção...")
        String reason,
        @Schema(example = "EM_ANDAMENTO")
        AdoptionStatus status) {
}
