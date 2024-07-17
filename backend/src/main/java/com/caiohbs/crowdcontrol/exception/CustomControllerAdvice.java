package com.caiohbs.crowdcontrol.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;


@ControllerAdvice
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RoleLimitExceededException.class)
    public ResponseEntity<ErrorDetails> handleRoleLimitExceededException(
            RoleLimitExceededException e
    ) {
        ErrorDetails errorResponse = new ErrorDetails();
        errorResponse.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NameTakenException.class)
    public ResponseEntity<ErrorDetails> handleUsernameTakenException(
            NameTakenException e
    ) {
        ErrorDetails errorResponse = new ErrorDetails();
        errorResponse.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException e
    ) {
        ErrorDetails errorResponse = new ErrorDetails();
        errorResponse.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity<ErrorDetails> handleValidationErrorException(
            ValidationErrorException e
    ) {
        ErrorDetails errorResponse = new ErrorDetails();
        errorResponse.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request
    ) {

        String errorsInValidation = ex.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorDetails errorDetails = new ErrorDetails(
                errorsInValidation
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }

}
