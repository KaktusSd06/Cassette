package com.example.cassette.adapters;

import com.example.cassette.MyClass.Movie;
import com.example.cassette.MyClass.SavedMovie;

import java.util.ArrayList;

public class InfoSavedFilm {

    private static InfoSavedFilm instance;
    private SavedMovie movie;

    private InfoSavedFilm() {

    }

    public static synchronized InfoSavedFilm getInstance() {
        if (instance == null) {
            instance = new InfoSavedFilm();
        }
        return instance;
    }

    public SavedMovie getMovies() {
        return movie;
    }

    public void setMovie(SavedMovie movie) {
        this.movie = movie;
    }
}
