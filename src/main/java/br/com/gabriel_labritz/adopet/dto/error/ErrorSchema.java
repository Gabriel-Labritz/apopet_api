package br.com.gabriel_labritz.adopet.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorSchema(
        @Schema(example = "Detalhe da mensagem do erro.")
        String detail,
        @Schema(example = "/example")
        String instance,
        @Schema(example = "404")
        Integer status,
        @Schema(example = "Not found")
        String title
) {
}
