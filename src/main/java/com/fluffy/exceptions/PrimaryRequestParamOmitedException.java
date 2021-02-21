package com.fluffy.exceptions;

/**
 * Клас винятку, що виникає в результаті невказання обов'язкового параметра
 * під час відправки запиту.
 * @author Сивоконь Вадим
 */
public class PrimaryRequestParamOmitedException extends RequestParamException {
    /**
     * Створює об'єкт винятку.
     */
    public PrimaryRequestParamOmitedException() {
        super();
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення
     * @param message текстове повідомлення
     */
    public PrimaryRequestParamOmitedException(String message) {
        super(message);
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення та
     * більш конкретного виключення (його обгортання).
     * @param message текстове повідомлення
     * @param cause більш точна причина виключення
     */
    public PrimaryRequestParamOmitedException(String message, Throwable cause) {
        super(message, cause);
    }
}
