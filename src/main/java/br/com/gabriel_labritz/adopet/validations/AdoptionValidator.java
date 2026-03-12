package br.com.gabriel_labritz.adopet.validations;

import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionRequestDto;

public interface AdoptionValidator {
    void validate(AdoptionRequestDto adoptionRequestDto);
}
