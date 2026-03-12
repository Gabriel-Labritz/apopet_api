package br.com.gabriel_labritz.adopet.services;

import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionRequestDto;
import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionResponseDto;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Adoption;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Pet;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Tutor;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.AdoptionRepository;
import br.com.gabriel_labritz.adopet.validations.AdoptionValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdoptionService {
    @Autowired
    AdoptionRepository adoptionRepository;

    @Autowired
    private TutorService tutorService;

    @Autowired
    private PetService petService;

    @Autowired
    private List<AdoptionValidator> validators;

    @Transactional
    public AdoptionResponseDto adopetPet(AdoptionRequestDto adoptionRequestDto) {
        Tutor tutor = tutorService.findTutorEntityById(adoptionRequestDto.tutor_id());
        Pet pet = petService.findPetEntityById(adoptionRequestDto.pet_id());

        validators.forEach(v -> v.validate(adoptionRequestDto));

        Adoption newAdoption = adoptionRepository.save(new Adoption(tutor, pet, adoptionRequestDto.reason()));
        return toAdoptionResponseDto(newAdoption);
    }

    public List<AdoptionResponseDto> getAllAdoptions() {
        return adoptionRepository.findAll().stream().map(this::toAdoptionResponseDto).toList();
    }

    public AdoptionResponseDto getAdoptionById(Long id) {
        Adoption adoption = findAdoptionEntityById(id);
        return toAdoptionResponseDto(adoption);
    }

    @Transactional
    public void approveAdoption(Long id) {
        Adoption adoption = findAdoptionEntityById(id);
        adoption.approve();
    }

    @Transactional
    public void disapproveAdoption(Long id) {
        Adoption adoption = findAdoptionEntityById(id);
        adoption.disapprove();
    }

    private Adoption findAdoptionEntityById(Long id) {
        return adoptionRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrosMessages.ADOPTION_NOTFOUND.getErrorMessage()));
    }

    private AdoptionResponseDto toAdoptionResponseDto(Adoption adoption) {
        return new AdoptionResponseDto(
                adoption.getId(),
                adoption.getDate(),
                adoption.getReason(),
                adoption.getStatus()
        );
    }
}
