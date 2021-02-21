package com.fluffy.dtos;

import java.util.Map;

public class FilmDTO {
    private String type;
    private String title;
    private String year;
    private String imdbID;

    private String rated;
    private String runtime;
    private String genre;
    private String released;

    private String plot;

    private String director;
    private String writer;
    private String actors;

    private String language;
    private String country;
    private String awards;

    private String production;
    private String boxOffice;

    private String metascore;
    private String imdbRating;
    private String imdbVotes;
    private Map<String, Map<String, String>> ratings;

    public FilmDTO() {

    }

    public FilmDTO(String type, String title, String year, String imdbID, String rated, String runtime, String genre, String released, String plot, String director, String writer, String actors, String language, String country, String awards, String production, String boxOffice, String metascore, String imdbRating, String imdbVotes, Map<String, Map<String, String>> ratings, String poster) {
        this.type = type;
        this.title = title;
        this.year = year;
        this.imdbID = imdbID;
        this.rated = rated;
        this.runtime = runtime;
        this.genre = genre;
        this.released = released;
        this.plot = plot;
        this.director = director;
        this.writer = writer;
        this.actors = actors;
        this.language = language;
        this.country = country;
        this.awards = awards;
        this.production = production;
        this.boxOffice = boxOffice;
        this.metascore = metascore;
        this.imdbRating = imdbRating;
        this.imdbVotes = imdbVotes;
        this.ratings = ratings;
        this.poster = poster;
    }

    // ???
    private String poster;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(String boxOffice) {
        this.boxOffice = boxOffice;
    }

    public String getMetascore() {
        return metascore;
    }

    public void setMetascore(String metascore) {
        this.metascore = metascore;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(String imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    // ???
    public Map<String, Map<String, String>> getRatings() {
        return ratings;
    }

    // ???
    public void setRatings(Map<String, Map<String, String>> ratings) {
        this.ratings = ratings;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
