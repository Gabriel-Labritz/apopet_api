package br.com.gabriel_labritz.adopet.dto.pets;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PetRequestDto(
        @Schema(example = "Artemis")
        @NotBlank(message = "O nome do pet é obrigatório.")
        @Size(min = 3, max = 50, message = "O nome do pet deve conter entre 3 e 50 caracteres.")
        String name,

        @Schema(example = "Gato")
        @NotBlank(message = "O tipo do pet é obrigatório.")
        String type,

        @Schema(example = "Vira-Lata")
        @NotBlank(message = "A raça do pet é obrigatória.")
        String breed,

        @Schema(example = "1")
        @NotNull(message = "A idade do pet é obrigatória.")
        @Positive
        Integer age,

        @Schema(example = "3.2")
        @Positive
        Double weight,

        @Schema(example = "Branco")
        @NotBlank(message = "A cor do pet é obrigatória.")
        String color,

        @Schema(example = "1")
        @NotNull(message = "Informe o abrigo do pet.")
        Long shelterId) {
}
