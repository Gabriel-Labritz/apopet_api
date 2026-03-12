package br.com.gabriel_labritz.adopet.validations;

import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionRequestDto;
import br.com.gabriel_labritz.adopet.enums.AdoptionStatus;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.AdoptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TutorLimitAdoptionsValidator implements AdoptionValidator{
    @Autowired
    private AdoptionRepository adoptionRepository;

    @Override
    public void validate(AdoptionRequestDto adoptionRequestDto) {
        if(adoptionRepository.countByTutorIdAndStatus(adoptionRequestDto.tutor_id(), AdoptionStatus.APROVADO) >= 3) {
            throw new AdoptionBusinessException(ErrosMessages.LIMIT_TUTOR_ADOPTIONS.getErrorMessage());
        }
    }
}
