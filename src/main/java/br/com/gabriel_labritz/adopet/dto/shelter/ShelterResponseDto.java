package br.com.gabriel_labritz.adopet.dto.shelter;

import io.swagger.v3.oas.annotations.media.Schema;

public record ShelterResponseDto(
        @Schema(example = "10")
        Long id,

        @Schema(example = "shelterexample@gmail.com")
        String email,

        @Schema(example = "1706641859")
        String phone) {
}
