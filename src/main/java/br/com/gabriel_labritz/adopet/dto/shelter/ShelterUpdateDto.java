package br.com.gabriel_labritz.adopet.dto.shelter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record ShelterUpdateDto(
        @Schema(example = "shelterexample2@gmail.com")
        @Email(message = "O e-mail informado é inválido.")
        String email,
        @Schema(example = "1706641852")
        @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter entre 10 ou 11 números")
        String phone) {
}
