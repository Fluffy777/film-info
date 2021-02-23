package com.fluffy.dtos;

/**
 * Клас, об'єкти якого інкапсулюють параметри запиту до RESTful додатку.
 * Забезпечує інформаційний обмін між шаром контролерів та сервісів.
 * @author Сивоконь Вадим
 */
public class GetFilmDataRequest {
    /**
     * Назва фільму.
     */
    private String title;

    /**
     * Рік випуску.
     */
    private String year;

    /**
     * Режим відображення сюжету: повний (full) або частковий (short).
     */
    private String plot;

    /**
     * IMDb ID фільму.
     */
    private String id;

    /**
     * Формат відповіді.
     */
    private String format;

    /**
     * Створює об'єкт для передачі даних про фільм.
     * @param title назва фільму
     * @param year рік випуску фільму
     * @param plot режим відображення сюжету
     * @param id IMDb ID фільму
     * @param format формат відповіді
     */
    public GetFilmDataRequest(final String title, final String year, final String plot, final String id, final String format) {
        this.title = title;
        this.year = year;
        this.plot = plot;
        this.id = id;
        this.format = format;
    }

    /**
     * Повертає назву фільму.
     * @return назва фільму
     */
    public String getTitle() {
        return title;
    }

    /**
     * Повертає рік випуску фільму у вигляді рядка - для можливості здійснення
     * подальшої валідації.
     * @return рік випуску
     */
    public String getYear() {
        return year;
    }

    /**
     * Повертає режим відображення сюжету.
     * @return режим відображення сюжету
     */
    public String getPlot() {
        return plot;
    }

    /**
     * Повертає IMDb ID фільму.
     * @return IMDb ID фільму
     */
    public String getId() {
        return id;
    }

    /**
     * Повертає формат відповіді.
     * @return формат відповіді
     */
    public String getFormat() {
        return format;
    }
}
