package br.com.gabriel_labritz.adopet.services;

import br.com.gabriel_labritz.adopet.dto.shelter.ShelterRequestDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterResponseDto;
import br.com.gabriel_labritz.adopet.dto.shelter.ShelterUpdateDto;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.DuplicationExistsException;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Shelter;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.ShelterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShelterService {
    @Autowired
    private ShelterRepository shelterRepository;

    public ShelterResponseDto shelterRegister(ShelterRequestDto shelterRequestDto) {
        if(shelterRepository.existsByEmailOrPhone(shelterRequestDto.email(), shelterRequestDto.phone())) {
            throw new DuplicationExistsException(ErrosMessages.DATA_ALREADY_USED.getErrorMessage());
        }

        Shelter shelter = new Shelter(shelterRequestDto);
        shelterRepository.save(shelter);
        return toShelterResponseDto(shelter);
    }

    public ShelterResponseDto getShelterById(Long id) {
        Shelter shelter = findShelterEntityById(id);
        return toShelterResponseDto(shelter);
    }

    public ShelterResponseDto updateShelterById(Long id, ShelterUpdateDto shelterUpdateDto) {
        Shelter shelter = findShelterEntityById(id);

        if(shelterUpdateDto.email() != null
                && !shelter.getEmail().equals(shelterUpdateDto.email())
                && shelterRepository.existsByEmail(shelterUpdateDto.email())) {
            throw new DuplicationExistsException(ErrosMessages.EMAIL_EXISTS.getErrorMessage());
        }

        if(shelterUpdateDto.phone() != null
                && !shelter.getPhone().equals(shelterUpdateDto.phone())
                && shelterRepository.existsByPhone(shelterUpdateDto.phone())) {
            throw new DuplicationExistsException(ErrosMessages.PHONE_EXISTS.getErrorMessage());
        }

        shelter.updateShelter(shelterUpdateDto);
        Shelter updatedShelter = shelterRepository.save(shelter);
        return toShelterResponseDto(updatedShelter);
    }

    public Shelter findShelterEntityById(Long id) {
        return shelterRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrosMessages.SHELTER_NOTFOUND.getErrorMessage()));
    }

    private ShelterResponseDto toShelterResponseDto(Shelter shelter) {
        return new ShelterResponseDto(shelter.getId(), shelter.getEmail(), shelter.getPhone());
    }
}
