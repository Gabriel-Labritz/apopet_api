package br.com.gabriel_labritz.adopet.dto.tutor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record TutorUpdateDto(
        @Email(message = "O e-mail informado é inválido.")
        String email,
        @Pattern(regexp = "\\d{11}", message = "O telefone deve conter 11 números")
        String phone) {
}
