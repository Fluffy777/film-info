package com.fluffy.exceptions;

/**
 * Клас виключення, необхідний для зручності групування своїх нащадків -
 * винятків, пов'язаних із проблемами, що можуть виникати під час передачі
 * значень параметрів запиту.
 * @author Сивоконь Вадим
 */
public class RequestParamException extends Exception {
    /**
     * Створює об'єкт винятку.
     */
    public RequestParamException() {
        super();
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення
     * @param message текстове повідомлення
     */
    public RequestParamException(String message) {
        super(message);
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення та
     * більш конкретного виключення (його обгортання).
     * @param message текстове повідомлення
     * @param cause більш точна причина виключення
     */
    public RequestParamException(String message, Throwable cause) {
        super(message, cause);
    }
}
