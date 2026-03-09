package br.com.gabriel_labritz.adopet.dto.adoption;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AdoptionRequestDto(
        @Schema(example = "1")
        @NotNull(message = "O tutor é obrigatório.")
        @Positive(message = "O id do tutor deve ser positivo.")
        Long tutor_id,

        @Schema(example = "5")
        @NotNull(message = "O pet é obrigatório.")
        @Positive(message = "O id do pet deve ser positivo.")
        Long pet_id,

        @Schema(example = "Motivo da adoção...")
        @NotBlank(message = "O motivo da adoção é obrigatório.")
        String reason
) {
}
