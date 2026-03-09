package br.com.gabriel_labritz.adopet.dto.tutor;

import io.swagger.v3.oas.annotations.media.Schema;

public record TutorResponseDto(
        @Schema(example = "João")
        String name,
        @Schema(example = "user@example.com")
        String email,
        @Schema(example = "24524743448")
        String phone
) {
}
