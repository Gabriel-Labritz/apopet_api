package br.com.gabriel_labritz.adopet.dto.pets;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

public record UpdatePetDto(
        @Schema(example = "2")
        @Positive(message = "Idade inválida, informe uma idade positiva.")
        Integer age,

        @Schema(example = "4.5")
        @Positive(message = "Peso inválido, informe um peso positivo.")
        Double weight,

        @Schema(example = "3")
        @Positive
        Long shelterId
) {
}
