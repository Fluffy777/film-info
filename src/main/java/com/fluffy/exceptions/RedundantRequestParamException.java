package com.fluffy.exceptions;

/**
 * Клас винятку, що виникає в результаті передачі зайвого параметра.
 * @author Сивоконь Вадим
 */
public class RedundantRequestParamException extends RequestParamException {
    /**
     * Створює об'єкт винятку.
     */
    public RedundantRequestParamException() {
        super();
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення
     * @param message текстове повідомлення
     */
    public RedundantRequestParamException(String message) {
        super(message);
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення та
     * більш конкретного виключення (його обгортання).
     * @param message текстове повідомлення
     * @param cause більш точна причина виключення
     */
    public RedundantRequestParamException(String message, Throwable cause) {
        super(message, cause);
    }
}
