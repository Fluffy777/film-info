package com.fluffy.exceptions;

/**
 * Клас винятку, що виникає в результаті спроби використання непідтримуваного
 * розширення зображення в якості постера.
 * @author Сивоконь Вадим
 */
public class BadPosterExtensionException extends Exception {
    /**
     * Створює об'єкт винятку.
     */
    public BadPosterExtensionException() {
        super();
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення
     * @param message текстове повідомлення
     */
    public BadPosterExtensionException(String message) {
        super(message);
    }

    /**
     * Створює об'єкт винятку із можливістю збереження текстового повідомлення та
     * більш конкретного виключення (його обгортання).
     * @param message текстове повідомлення
     * @param cause більш точна причина виключення
     */
    public BadPosterExtensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
