package br.com.gabriel_labritz.adopet.services;

import br.com.gabriel_labritz.adopet.dto.pets.PetRequestDto;
import br.com.gabriel_labritz.adopet.dto.pets.PetResponseDto;
import br.com.gabriel_labritz.adopet.dto.pets.UpdatePetDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterResponseDto;
import br.com.gabriel_labritz.adopet.enums.TypePet;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.AdoptionBusinessException;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Pet;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Shelter;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.PetRepository;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.ShelterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;

    @Autowired
    ShelterRepository shelterRepository;

    public PetResponseDto registerPet(PetRequestDto petRequestDto) {
        Shelter shelter = findShelterEntityById(petRequestDto.shelterId());

        Pet pet = new Pet();
        pet.setName(petRequestDto.name());
        pet.setType(TypePet.toPetType(petRequestDto.type()));
        pet.setBreed(petRequestDto.breed());
        pet.setAge(petRequestDto.age());
        pet.setWeight(petRequestDto.weight());
        pet.setColor(petRequestDto.color());
        pet.setShelter(shelter);

        petRepository.save(pet);
        return toPetResponseDto(pet);
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

        applyPetsUpdates(updatePetDto, pet);

        Pet petUpdated = petRepository.save(pet);
        return toPetResponseDto(petUpdated);
    }

    public Pet findPetEntityById(Long id) {
        return petRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrosMessages.PET_NOTFOUND.getErrorMessage()));
    }

    private void applyPetsUpdates(UpdatePetDto updatePetDto, Pet pet) {
        if(updatePetDto.shelterId() != null) {
            Shelter shelter = findShelterEntityById(updatePetDto.shelterId());
            pet.setShelter(shelter);
        }

        if (updatePetDto.weight() != null) {
            pet.setWeight(updatePetDto.weight());
        }

        if(updatePetDto.age() != null) {
            pet.setAge(updatePetDto.age());
        }
    }

    private Shelter findShelterEntityById(Long id) {
        return shelterRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(ErrosMessages.SHELTER_NOTFOUND.getErrorMessage()));
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
