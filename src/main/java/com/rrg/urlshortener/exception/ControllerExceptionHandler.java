package com.rrg.urlshortener.exception;

import com.rrg.urlshortener.openapi.model.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneOffset;
import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MissingFieldException.class)
    public ResponseEntity<ErrorDto> missingFieldExceptionHandler(MissingFieldException e) {
        return createResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<ErrorDto> invalidFieldExceptionHandler(InvalidFieldException e) {
        return createResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> shortUrlNotFoundException(ResourceNotFoundException e) {
        return createResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ShortUrlIdGenerationException.class)
    public ResponseEntity<ErrorDto> shortUrlIdGenerationException(ShortUrlIdGenerationException e) {
        return createResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> globalExceptionHandler(Exception e) {
        return createResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDto> createResponseEntity(String message, HttpStatus status) {
        var date = new Date();
        var errorDto = new ErrorDto();
        errorDto.setTimestamp(date.toInstant().atOffset(ZoneOffset.UTC));
        errorDto.setStatusCode(status.value());
        errorDto.setMessage(message);
        return new ResponseEntity<>(errorDto, status);
    }
}
