package br.com.gabriel_labritz.adopet.exceptions;

public class AdoptionBusinessException extends RuntimeException {
    public AdoptionBusinessException(String message) {
        super(message);
    }
}
