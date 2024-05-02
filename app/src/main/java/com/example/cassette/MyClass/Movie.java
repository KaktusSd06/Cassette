package com.example.cassette.MyClass;

public class Movie {
    double imdb;
    String about, director, filmCompany, imgSrc, title, type, firebaseKey;
    String[] genres, country, cast;
    int ageRating, year, duration;

    public Movie(){}
    public Movie(String about, int ageRating, String castString, String country, String director, int duration,
                 String filmCompany, String genres, double imdb, String imgSrc, String title, String type, int year)
    {
        this.imdb = imdb;
        this.about = about;
        this.country = country.split(",\\s*");
        this.director = director;
        this.duration = duration;
        this.filmCompany = filmCompany;
        this.imgSrc = imgSrc;
        this.title = title;
        this.type = type;
        this.cast = castString.split(",\\s*");
        this.genres = genres.split(",\\s*");
        this.ageRating = ageRating;
        this.year = year;
        firebaseKey = "0";
    }

    public Movie(String about, int ageRating, String castString, String country, String director, int duration,
                 String filmCompany, String genres, double imdb, String imgSrc, String title, String type, int year, String firebaseKey)
    {
        this.imdb = imdb;
        this.about = about;
        this.country = country.split(",\\s*");
        this.director = director;
        this.duration = duration;
        this.filmCompany = filmCompany;
        this.imgSrc = imgSrc;
        this.title = title;
        this.type = type;
        this.cast = castString.split(",\\s*");
        this.genres = genres.split(",\\s*");
        this.ageRating = ageRating;
        this.year = year;
        this.firebaseKey = firebaseKey;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String[] getGenres() {
        return genres;
    }

    public String getGenresString(){
        return String.join(", ", genres);
    }

    public double getImdb() {
        return imdb;
    }

    public void setImdb(double imdb) {
        this.imdb = imdb;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String GetStringDuraction(){return String.valueOf(duration);}
    public String getCountry() {
        return String.join(", ", country);
    }

    public void setCountry(String country) {
        this.country = country.split(",\\s*");
    }

    public String getCastString(){
        return String.join(", ", cast);
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFilmCompany() {
        return filmCompany;
    }

    public void setFilmCompany(String filmCompany) {
        this.filmCompany = filmCompany;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getCast() {
        return cast;
    }

    public void setCast(String castString) {
        this.cast = this.cast = castString.split(",\\s*");
    }

//    public String[] getGenres() {
//        return genres;
//    }
//
//    public void setGenres(String[] genres) {
//        this.genres = genres;
//    }

    public int getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(int ageRating) {
        this.ageRating = ageRating;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

}
