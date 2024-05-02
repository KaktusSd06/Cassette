package com.example.cassette.adapters;

import com.example.cassette.MyClass.SavedMovie;


import java.util.ArrayList;

public class SavedListAdapter {
    private static SavedListAdapter instance;
    private ArrayList<SavedMovie> savedMovies;

    private SavedListAdapter() {
        savedMovies = new ArrayList<>();
    }

    public static synchronized SavedListAdapter getInstance() {
        if (instance == null) {
            instance = new SavedListAdapter();
        }
        return instance;
    }

    public ArrayList<SavedMovie> getSavedMovies() {
        return savedMovies;
    }

    public void saveFilm(SavedMovie newProduct) {
        savedMovies.add(newProduct);
    }

}