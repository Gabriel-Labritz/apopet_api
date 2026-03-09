package br.com.gabriel_labritz.adopet.dto.pets;

import br.com.gabriel_labritz.adopet.dto.shelter.ShelterResponseDto;
import br.com.gabriel_labritz.adopet.enums.TypePet;
import io.swagger.v3.oas.annotations.media.Schema;

public record PetResponseDto(
        @Schema(example = "12")
        Long id,

        @Schema(example = "Artemis")
        String name,

        @Schema(example = "Gato")
        TypePet type,

        @Schema(example = "Vira-Lata")
        String breed,

        @Schema(example = "1")
        Integer age,

        @Schema(example = "3.2")
        Double weight,

        @Schema(example = "Branco")
        String color,

        ShelterResponseDto shelter
) {
}
