package com.fluffy.handlers;

import com.fluffy.exceptions.FilmServiceException;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.concurrent.ExecutionException;

/**
 * Клас, призначений для обробки винятків.
 * @author Сивоконь Вадим
 */
@ControllerAdvice
public class PrimaryExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * Для забезпечення логування.
     */
    private static final Logger logger = Logger.getLogger(PrimaryExceptionHandler.class);

    /**
     * Обробник винятку FilmServiceException.
     * @param ex виняток
     * @param webRequest запит
     * @return тіло відповіді
     */
    @ExceptionHandler(FilmServiceException.class)
    public ResponseEntity<?> handleFilmServiceException(final FilmServiceException ex, final WebRequest webRequest) {
        logger.info("Виняток " + ex.getClass().getSimpleName() + " опрацьований");
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.TEXT_HTML)
                .body(String.format("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Помилка</title></head><body><h1>Помилка</h1><p>" +
                        "<u>%s</u> (<em>%s</em>)</p></body></html>", ex.getMessage(), ex.getCause().getMessage()));
    }

    /**
     * Обробник винятків ExecutionException, InterruptedException.
     * @param ex виняток
     * @param webRequest запит
     * @return тіло відповіді
     */
    @ExceptionHandler({ExecutionException.class, InterruptedException.class})
    public ResponseEntity<?> handleFilmServiceException(final Exception ex, final WebRequest webRequest) {
        logger.info("Виняток " + ex.getClass().getSimpleName() + " опрацьований");
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.TEXT_HTML)
                .body(String.format("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Помилка</title></head><body><h1>Внутрішня помилка багатопоточності</h1><p>" +
                        "%s</p></body></html>", ex.getMessage()));
    }
}
