package br.com.gabriel_labritz.adopet.services;

import br.com.gabriel_labritz.adopet.dto.tutor.TutorRequestDto;
import br.com.gabriel_labritz.adopet.dto.tutor.TutorResponseDto;
import br.com.gabriel_labritz.adopet.dto.tutor.TutorUpdateDto;
import br.com.gabriel_labritz.adopet.enums.errors.ErrosMessages;
import br.com.gabriel_labritz.adopet.exceptions.DuplicationExistsException;
import br.com.gabriel_labritz.adopet.exceptions.NotFoundException;
import br.com.gabriel_labritz.adopet.infrastructure.entities.Tutor;
import br.com.gabriel_labritz.adopet.infrastructure.repositories.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TutorService {
    @Autowired
    private TutorRepository tutorRepository;

    public TutorResponseDto register(TutorRequestDto tutorRequestDto) {
        if(tutorRepository.existsByEmail(tutorRequestDto.email())) {
            throw new DuplicationExistsException(ErrosMessages.EMAIL_EXISTS.getErrorMessage());
        }

        Tutor tutor = tutorRepository.save(new Tutor(tutorRequestDto));
        return toTutorResponseDto(tutor);
    }

    public TutorResponseDto getTutorById(Long id) {
        Tutor tutor = findTutorEntityById(id);
        return toTutorResponseDto(tutor);
    }

    public TutorResponseDto updateTutorById(Long id, TutorUpdateDto tutorUpdateDto) {
        Tutor tutor = findTutorEntityById(id);

        if (tutorUpdateDto.email() != null
                && !tutor.getEmail().equals(tutorUpdateDto.email())
                && tutorRepository.existsByEmail(tutorUpdateDto.email())) {
            throw new DuplicationExistsException(ErrosMessages.EMAIL_EXISTS.getErrorMessage());
        }

        tutor.updateTutor(tutorUpdateDto);
        Tutor tutorUpdated = tutorRepository.save(tutor);
        return toTutorResponseDto(tutorUpdated);
    }

    public Tutor findTutorEntityById(Long id) {
        return tutorRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrosMessages.TUTOR_NOTFOUND.getErrorMessage()));
    }

    private TutorResponseDto toTutorResponseDto(Tutor tutor) {
        return new TutorResponseDto(
                tutor.getName(),
                tutor.getEmail(),
                tutor.getPhone()
        );
    }
}
