package com.example.cassette.MyClass;

public class SavedMovie {
    Movie movie;
    boolean isWatched;
    double rating;

    public String getKeyFirebase() {
        return keyFirebase;
    }

    String keyFirebase;

    public SavedMovie(Movie movie, boolean isWatched, double rairing, String keyFirebase) {
        this.movie = movie;
        this.isWatched = isWatched;
        this.keyFirebase = keyFirebase;
        setRating(rairing);
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public boolean isWatched() {
        return isWatched;
    }

    public void setWatched(boolean watched) {
        isWatched = watched;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if(rating > 5)
            this.rating = 5;
        else if(rating <0)
            this.rating = 0;
        else
            this.rating = rating;
    }
}
