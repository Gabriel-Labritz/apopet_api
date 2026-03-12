package br.com.gabriel_labritz.adopet.validations;

import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionRequestDto;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Pet;
import br.com.gabriel_labritz.adopet.services.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PetAlreadyAdoptedValidator implements AdoptionValidator{
    @Autowired
    private PetService petService;

    @Override
    public void validate(AdoptionRequestDto adoptionRequestDto) {
        Pet pet = petService.findPetEntityById(adoptionRequestDto.pet_id());

        if(pet.getAdopted()) {
            throw new AdoptionBusinessException(ErrosMessages.PET_ADOPTED.getErrorMessage());
        }
    }
}
