package br.com.gabriel_labritz.adopet.exceptions;

public class DuplicationExistsException extends RuntimeException {
    public DuplicationExistsException(String message) {
        super(message);
    }
}
