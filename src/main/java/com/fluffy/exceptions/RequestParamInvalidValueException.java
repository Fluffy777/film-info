package com.fluffy.exceptions;

/**
 * Клас винятку, що виникає в результаті вказання для параметра некоректного
 * значення.
 * @author Сивоконь Вадим
 */
public class RequestParamInvalidValueException extends RequestParamException {
    /**
     * Створює об'єкт винятку.
     */
    public RequestParamInvalidValueException() {
        super();
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення
     * @param message текстове повідомлення
     */
    public RequestParamInvalidValueException(String message) {
        super(message);
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення та
     * більш конкретного виключення (його обгортання).
     * @param message текстове повідомлення
     * @param cause більш точна причина виключення
     */
    public RequestParamInvalidValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
