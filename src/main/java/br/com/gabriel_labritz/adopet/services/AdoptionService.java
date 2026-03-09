package br.com.gabriel_labritz.adopet.services;

import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionRequestDto;
import br.com.gabriel_labritz.adopet.dto.adoption.AdoptionResponseDto;
import br.com.gabriel_labritz.adopet.enums.AdoptionStatus;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Adoption;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Pet;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Tutor;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.AdoptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdoptionService {
    @Autowired
    AdoptionRepository adoptionRepository;

    @Autowired
    private TutorService tutorService;

    @Autowired
    private PetService petService;

    @Transactional
    public AdoptionResponseDto adopetPet(AdoptionRequestDto adoptionRequestDto) {
        Tutor tutor = tutorService.findTutorEntityById(adoptionRequestDto.tutor_id());
        Pet pet = petService.findPetEntityById(adoptionRequestDto.pet_id());

        if(pet.getAdopted()) {
            throw new AdoptionBusinessException(ErrosMessages.PET_ADOPTED.getErrorMessage());
        }

        if(adoptionRepository.existsByPetIdAndStatus(adoptionRequestDto.pet_id(), AdoptionStatus.EM_ANDAMENTO)) {
            throw new AdoptionBusinessException(ErrosMessages.ADOPTION_IN_PROGRESS.getErrorMessage());
        }

        if(adoptionRepository.countByTutorIdAndStatus(adoptionRequestDto.tutor_id(), AdoptionStatus.APROVADO) >= 3) {
            throw new AdoptionBusinessException(ErrosMessages.LIMIT_TUTOR_ADOPTIONS.getErrorMessage());
        }

        if(adoptionRepository.existsByTutorIdAndPetIdAndStatusEquals(
                adoptionRequestDto.tutor_id(),
                adoptionRequestDto.pet_id(),
                AdoptionStatus.REPROVADO)
        ) {
            throw new AdoptionBusinessException(ErrosMessages.ADOPTION_ALREADY_REJECT.getErrorMessage());
        }

        Adoption adoption = new Adoption();
        adoption.setDate(LocalDate.now());
        adoption.setReason(adoptionRequestDto.reason());
        adoption.setTutor(tutor);
        adoption.setPet(pet);
        adoption.setStatus(AdoptionStatus.EM_ANDAMENTO);

        adoptionRepository.save(adoption);
        return toAdoptionResponseDto(adoption);
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
