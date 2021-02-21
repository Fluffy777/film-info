package com.fluffy.exceptions;

/**
 * Клас-обгортка винятків для тих, що можуть виникати в рамках виконання
 * бізнес-логіки відповідного сервісу. Необхідний для можливості підтримки
 * рівня абстракції виконуваних методів.
 * @author Сивоконь Вадим
 */
public class FilmServiceException extends Exception {
    /**
     * Створює об'єкт винятку.
     */
    public FilmServiceException() {
        super();
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення
     * @param message текстове повідомлення
     */
    public FilmServiceException(String message) {
        super(message);
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення та
     * більш конкретного виключення (його обгортання).
     * @param message текстове повідомлення
     * @param cause більш точна причина виключення
     */
    public FilmServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
