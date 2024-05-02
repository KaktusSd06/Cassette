package com.example.cassette.adapters;

import android.icu.text.IDNA;

import com.example.cassette.MyClass.Movie;
import com.example.cassette.MyClass.SavedMovie;

import java.util.ArrayList;

public class InfoFilm {

    private static InfoFilm instance;
    private Movie movie;

    private InfoFilm() {

    }

    public static synchronized InfoFilm getInstance() {
        if (instance == null) {
            instance = new InfoFilm();
        }
        return instance;
    }

    public Movie getMovies() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
