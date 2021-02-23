package com.fluffy.exceptions;

/**
 * Клас винятку, що виникає в результаті відсутності даних для відображення
 * в документі.
 * @author Сивоконь Вадим
 */
public class NoDataFoundException extends Exception {
    /**
     * Створює об'єкт винятку.
     */
    public NoDataFoundException() {
        super();
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення
     * @param message текстове повідомлення
     */
    public NoDataFoundException(String message) {
        super(message);
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення та
     * більш конкретного виключення (його обгортання).
     * @param message текстове повідомлення
     * @param cause більш точна причина виключення
     */
    public NoDataFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
