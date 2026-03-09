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
        if(shelterRepository.existsByEmail(shelterRequestDto.email())) {
            throw new DuplicationExistsException(ErrosMessages.EMAIL_EXISTS.getErrorMessage());
        }

        if (shelterRepository.existsByPhone(shelterRequestDto.phone())) {
            throw new DuplicationExistsException(ErrosMessages.PHONE_EXISTS.getErrorMessage());
        }

        Shelter shelter = new Shelter();
        shelter.setEmail(shelterRequestDto.email());
        shelter.setPhone(shelterRequestDto.phone());

        shelterRepository.save(shelter);
        return toShelterResponseDto(shelter);
    }

    public ShelterResponseDto getShelterById(Long id) {
        Shelter shelter = findShelterEntityById(id);
        return toShelterResponseDto(shelter);
    }

    public ShelterResponseDto updateShelterById(Long id, ShelterUpdateDto updateDto) {
        Shelter shelter = findShelterEntityById(id);

        applyShelterUpdates(updateDto, shelter);

        Shelter updatedShelter = shelterRepository.save(shelter);
        return toShelterResponseDto(updatedShelter);
    }

    private Shelter findShelterEntityById(Long id) {
        return shelterRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrosMessages.SHELTER_NOTFOUND.getErrorMessage()));
    }

    private void applyShelterUpdates(ShelterUpdateDto dto, Shelter shelter) {
        if(dto.email() != null) {
            if(!shelter.getEmail().equals(dto.email()) && shelterRepository.existsByEmail(dto.email())) {
                throw new DuplicationExistsException(ErrosMessages.EMAIL_EXISTS.getErrorMessage());
            }

            shelter.setEmail(dto.email());
        }

        if(dto.phone() != null) {
            if(!shelter.getPhone().equals(dto.phone()) && shelterRepository.existsByPhone(dto.phone())) {
                throw new DuplicationExistsException(ErrosMessages.PHONE_EXISTS.getErrorMessage());
            }

            shelter.setPhone(dto.phone());
        }
    }

    private ShelterResponseDto toShelterResponseDto(Shelter shelter) {
        return new ShelterResponseDto(shelter.getId(), shelter.getEmail(), shelter.getPhone());
    }
}
