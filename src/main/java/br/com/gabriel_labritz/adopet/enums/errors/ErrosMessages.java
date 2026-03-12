package br.com.gabriel_labritz.adopet.enums.errors;

import lombok.Getter;

@Getter
public enum ErrosMessages {
    EMAIL_EXISTS("O e-mail informado já está em uso."),
    PHONE_EXISTS("O telefone informado já está em uso."),
    TUTOR_NOTFOUND("O Tutor não foi encontrado."),
    SHELTER_NOTFOUND("O abrigo não foi encontrado."),
    PET_NOTFOUND("O pet não foi encontrado."),
    ADOPTION_NOTFOUND("A adoção não foi encontrada."),
    PET_ADOPTED("O pet já foi adotado."),
    ADOPTION_IN_PROGRESS("Já existe uma adoção em andamento para esse pet."),
    LIMIT_TUTOR_ADOPTIONS("Esse tutor já atingiu o limite de adoções permitidas."),
    ADOPTION_ALREADY_REJECT("Esse tutor já teve uma solicitação reprovada para esse pet."),
    UPDATE_PET_ADOPTED("Não é possível atualizar um pet já adotado."),
    DATA_ALREADY_USED("Os dados informados de email e/ou telefone já estão sendo utilizados.");

    private String errorMessage;

    ErrosMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
