package com.fluffy.dtos;

import java.util.Map;

/**
 * Клас, що надає можливість зберігати розпарсені дані про фільм, які були
 * отримані від використовуваного джерела.
 * @author Сивоконь Вадим
 */
public class FilmDTO {
    /**
     * Результат опрацювання запиту: True або False.
     */
    private String response;

    /**
     * Тип контенту: фільм (movie), серії (series), епізоди (episode).
     */
    private String type;

    /**
     * Назва фільму.
     */
    private String title;

    /**
     * Рік випуску фільму.
     */
    private String year;

    /**
     * IMDb ID.
     */
    private String imdbID;

    /**
     * Рейтинг вмісту (визначення вікової категорії).
     */
    private String rated;

    /**
     * Тривалість фільму.
     */
    private String runtime;

    /**
     * Жанр фільму.
     */
    private String genre;

    /**
     * Дата випуску.
     */
    private String released;

    /**
     * Сюжет.
     */
    private String plot;

    /**
     * URL-посилання на постер.
     */
    private String poster;

    /**
     * Директор.
     */
    private String director;

    /**
     * Сценарист.
     */
    private String writer;

    /**
     * Актори.
     */
    private String actors;

    /**
     * Мова фільму.
     */
    private String language;

    /**
     * Країна-виробник фільму.
     */
    private String country;

    /**
     * Отримані нагороди.
     */
    private String awards;

    /**
     * Студія - виробник фільму.
     */
    private String production;

    /**
     * Касові збори.
     */
    private String boxOffice;

    /**
     * Рейтинг на www.metacritic.com.
     */
    private String metascore;

    /**
     * Рейтинг на www.imdb.com.
     */
    private String imdbRating;

    /**
     * Кількість голосів на www.imdb.com.
     */
    private String imdbVotes;

    /**
     * Рейтинги на різних ресурсах.
     */
    private Map<String, Map<String, String>> ratings;

    /**
     * Створює порожній DTO фільму.
     */
    public FilmDTO() {
    }

    /**
     * Повертає результат опрацювання запиту.
     * @return результат опрацювання запиту
     */
    public String getResponse() {
        return response;
    }

    /**
     * Встановлює результат опрацювання запиту.
     * @param response результат опрацювання запиту
     */
    public void setResponse(final String response) {
        this.response = response;
    }

    /**
     * Повертає тип контенту.
     * @return тип контенту
     */
    public String getType() {
        return type;
    }

    /**
     * Встановлює тип контенту.
     * @param type тип контенту
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Повертає назву фільму.
     * @return назва фільму
     */
    public String getTitle() {
        return title;
    }

    /**
     * Встановлює назву фільму.
     * @param title назва фільму
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Повертає рік випуску фільму.
     * @return рік випуску фільму
     */
    public String getYear() {
        return year;
    }

    /**
     * Встановлює рік випуску фільму.
     * @param year рік випуску фільму
     */
    public void setYear(final String year) {
        this.year = year;
    }

    /**
     * Повертає IMDb ID.
     * @return IMDb ID
     */
    public String getImdbID() {
        return imdbID;
    }

    /**
     * Встановлює IMDb ID.
     * @param imdbID IMDb ID
     */
    public void setImdbID(final String imdbID) {
        this.imdbID = imdbID;
    }

    /**
     * Повертає вікову категорію.
     * @return вікова категорія
     */
    public String getRated() {
        return rated;
    }

    /**
     * Встановлює вікову категорію.
     * @param rated вікова категорія
     */
    public void setRated(final String rated) {
        this.rated = rated;
    }

    /**
     * Повертає тривалість фільму.
     * @return тривалість фільму
     */
    public String getRuntime() {
        return runtime;
    }

    /**
     * Встановлює тривалість фільму.
     * @param runtime тривалість фільму
     */
    public void setRuntime(final String runtime) {
        this.runtime = runtime;
    }

    /**
     * Повертає жанр фільму.
     * @return жанр фільму
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Встановлює жанр фільму.
     * @param genre жанр фільму
     */
    public void setGenre(final String genre) {
        this.genre = genre;
    }

    /**
     * Повертає дату випуску.
     * @return дата випуску
     */
    public String getReleased() {
        return released;
    }

    /**
     * Встановлює дату випуску.
     * @param released дата випуску
     */
    public void setReleased(final String released) {
        this.released = released;
    }

    /**
     * Повертає сюжет.
     * @return сюжет
     */
    public String getPlot() {
        return plot;
    }

    /**
     * Встановлює сюжет.
     * @param plot сюжет
     */
    public void setPlot(final String plot) {
        this.plot = plot;
    }

    /**
     * Повертає URL-посилання на постер.
     * @return URL-посилання на постер
     */
    public String getPoster() {
        return poster;
    }

    /**
     * Встановлює URL-посилання на постер.
     * @param poster URL-посилання на постер
     */
    public void setPoster(final String poster) {
        this.poster = poster;
    }

    /**
     * Повертає директора.
     * @return директор
     */
    public String getDirector() {
        return director;
    }

    /**
     * Встановлює директора.
     * @param director директор
     */
    public void setDirector(final String director) {
        this.director = director;
    }

    /**
     * Повертає сценариста.
     * @return сценарист
     */
    public String getWriter() {
        return writer;
    }

    /**
     * Встановлює сценариста.
     * @param writer сценарист
     */
    public void setWriter(final String writer) {
        this.writer = writer;
    }

    /**
     * Повертає акторів.
     * @return актори
     */
    public String getActors() {
        return actors;
    }

    /**
     * Встановлює акторів.
     * @param actors актори
     */
    public void setActors(final String actors) {
        this.actors = actors;
    }

    /**
     * Повертає мову фільму.
     * @return мова фільму
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Встановлює мову фільму.
     * @param language мова фільму
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * Повертає країну-виробника фільму.
     * @return країна-виробник фільму
     */
    public String getCountry() {
        return country;
    }

    /**
     * Встановлює країну-виробника фільму.
     * @param country країна-виробник фільму
     */
    public void setCountry(final String country) {
        this.country = country;
    }

    /**
     * Повертає нагороди.
     * @return нагороди
     */
    public String getAwards() {
        return awards;
    }

    /**
     * Встановлює нагороди.
     * @param awards нагороди
     */
    public void setAwards(final String awards) {
        this.awards = awards;
    }

    /**
     * Повертає студію - виробника фільму.
     * @return студія - виробник фільму
     */
    public String getProduction() {
        return production;
    }

    /**
     * Встановлює студію - виробника фільму.
     * @param production студія - виробник фільму
     */
    public void setProduction(final String production) {
        this.production = production;
    }

    /**
     * Повертає касові збори.
     * @return касові збори
     */
    public String getBoxOffice() {
        return boxOffice;
    }

    /**
     * Встановлює касові збори.
     * @param boxOffice касові збори
     */
    public void setBoxOffice(final String boxOffice) {
        this.boxOffice = boxOffice;
    }

    /**
     * Повертає рейтинг на www.metacritic.com.
     * @return рейтинг на www.metacritic.com
     */
    public String getMetascore() {
        return metascore;
    }

    /**
     * Встановлює рейтинг як на www.metacritic.com.
     * @param metascore рейтинг на www.metacritic.com
     */
    public void setMetascore(final String metascore) {
        this.metascore = metascore;
    }

    /**
     * Повертає рейтинг на www.imdb.com.
     * @return рейтинг на www.imdb.com
     */
    public String getImdbRating() {
        return imdbRating;
    }

    /**
     * Встановлює рейтинг як на www.imdb.com.
     * @param imdbRating рейтинг на www.imdb.com
     */
    public void setImdbRating(final String imdbRating) {
        this.imdbRating = imdbRating;
    }

    /**
     * Повертає кількість голосів на www.imdb.com.
     * @return кількість голосів на www.imdb.com
     */
    public String getImdbVotes() {
        return imdbVotes;
    }

    /**
     * Встановлює кількість голосів як на www.imdb.com.
     * @param imdbVotes кількість голосів на www.imdb.com
     */
    public void setImdbVotes(final String imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    /**
     * Повертає рейтинги на різних ресурсах.
     * @return рейтинги на різних ресурсах
     */
    public Map<String, Map<String, String>> getRatings() {
        return ratings;
    }

    /**
     * Встановлює рейтинги як на різних ресурсах.
     * @param ratings рейтинги на різних ресурсах
     */
    public void setRatings(final Map<String, Map<String, String>> ratings) {
        this.ratings = ratings;
    }
}
