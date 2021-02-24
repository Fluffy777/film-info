package com.fluffy.handlers;

import com.fluffy.exceptions.FilmServiceException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class PrimaryExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(FilmServiceException.class)
    public ResponseEntity<?> handleFilmServiceException(FilmServiceException ex, WebRequest webRequest) {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.TEXT_HTML)
                .body(String.format("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Помилка</title></head><body><h1>Помилка</h1><p>" +
                        "<u>%s</u> (<em>%s</em>)</p></body></html>", ex.getMessage(), ex.getCause().getMessage()));
    }
}
