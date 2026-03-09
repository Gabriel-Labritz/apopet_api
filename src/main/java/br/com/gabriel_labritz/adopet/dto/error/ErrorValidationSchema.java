package br.com.gabriel_labritz.adopet.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record ErrorValidationSchema(
        @Schema(example = "Dados inválidos.")
        String detail,
        @Schema(example = "/example")
        String instance,
        @Schema(example = "400")
        Integer status,
        @Schema(example = "Bad request")
        String title,
        @Schema(example = """
                {
                              "email": "Email inválido.",
                              "name": "O nome é obrigatório."
                            }
                """)
        Map<String, String> errors
) {
}
