package com.fluffy.exceptions;

/**
 * Клас винятку, що виникає в результаті вказання параметрів запиту, що не
 * можуть бути наявні одночасно.
 * @author Сивоконь Вадим
 */
public class RequestParamsConflictException extends RequestParamException {
    /**
     * Створює об'єкт винятку.
     */
    public RequestParamsConflictException() {
        super();
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення
     * @param message текстове повідомлення
     */
    public RequestParamsConflictException(String message) {
        super(message);
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення та
     * більш конкретного виключення (його обгортання).
     * @param message текстове повідомлення
     * @param cause більш точна причина виключення
     */
    public RequestParamsConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
