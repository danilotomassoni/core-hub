package io.github.danilotomassoni.core_hub.auth_service.exception;

public class EmailAlreadyExistsException extends  RuntimeException{

    public EmailAlreadyExistsException(String message) {
        super(message);
    }

}
