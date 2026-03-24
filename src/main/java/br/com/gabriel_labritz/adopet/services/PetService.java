package br.com.gabriel_labritz.adopet.services;

import br.com.gabriel_labritz.adopet.dto.pets.PetRequestDto;
import br.com.gabriel_labritz.adopet.dto.pets.PetResponseDto;
import br.com.gabriel_labritz.adopet.dto.pets.UpdatePetDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterResponseDto;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Pet;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Shelter;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;

    @Autowired
    ShelterService shelterService;

    public PetResponseDto registerPet(PetRequestDto petRequestDto) {
        Shelter shelter = shelterService.findShelterEntityById(petRequestDto.shelterId());
        Pet newPet = petRepository.save(new Pet(petRequestDto, shelter));
        return toPetResponseDto(newPet);
    }

    public List<PetResponseDto> getAllPets() {
        return petRepository.findAll().stream().map(this::toPetResponseDto).toList();
    }

    public List<PetResponseDto> getAllPetsAvaliable() {
        return petRepository.findByAdoptedFalseOrderByIdDesc().stream().map(this::toPetResponseDto).toList();
    }

    public PetResponseDto getPetById(Long id) {
        Pet pet = findPetEntityById(id);
        return toPetResponseDto(pet);
    }

    public PetResponseDto updatePetById(Long id, UpdatePetDto updatePetDto) {
        Pet pet = findPetEntityById(id);

        if(pet.getAdopted()) {
            throw new AdoptionBusinessException(ErrosMessages.UPDATE_PET_ADOPTED.getErrorMessage());
        }

        if(updatePetDto.shelterId() != null) {
            Shelter shelter = shelterService.findShelterEntityById(updatePetDto.shelterId());
            pet.changeShelter(shelter);
        }

        pet.updatePet(updatePetDto);
        Pet petUpdated = petRepository.save(pet);
        return toPetResponseDto(petUpdated);
    }

    public Pet findPetEntityById(Long id) {
        return petRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrosMessages.PET_NOTFOUND.getErrorMessage()));
    }

    private PetResponseDto toPetResponseDto(Pet pet) {
        return new PetResponseDto(
                pet.getId(),
                pet.getName(),
                pet.getType(),
                pet.getBreed(),
                pet.getAge(),
                pet.getWeight(),
                pet.getColor(),
                new ShelterResponseDto(
                        pet.getShelter().getId(),
                        pet.getShelter().getEmail(),
                        pet.getShelter().getPhone())
        );
    }
}
