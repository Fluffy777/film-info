package com.fluffy.dtos;

/**
 * Клас, надає можливість створення об'єктів передачи даних (DTO) фільмів.
 * Забезпечує інформаційний обмін між контролерами та сервісами.
 * @author Сивоконь Вадим
 */
public class GetFilmDataRequest {
    /**
     * Назва фільму.
     */
    private String title;

    /**
     * Рік випуску. Рядок, щоб мати можливість перевірити на null.
     */
    private String year;

    /**
     * Тип співпадіння.
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
     * Створює об'єкт передачі даних про фільм.
     * @param title назва фільму
     * @param year рік випуску фільму
     * @param plot тип співпадіння
     * @param id IMDb ID фільму
     * @param format формат відповіді
     */
    public GetFilmDataRequest(String title, String year, String plot, String id, String format) {
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
     * Повертає тип співпадіння.
     * @return тип співпадіння
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
     * Повертає формат відповіді
     * @return формат відповіді
     */
    public String getFormat() {
        return format;
    }
}
