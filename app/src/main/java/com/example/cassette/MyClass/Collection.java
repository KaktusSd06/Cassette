package com.example.cassette.MyClass;

import java.util.ArrayList;

public class Collection {
    String title;
    ArrayList<SavedMovie> savedMoviesInTag;
    String keyFirebase;

    public String getKeyFirebase() {
        return keyFirebase;
    }

    public void setKeyFirebase(String keyFirebase) {
        this.keyFirebase = keyFirebase;
    }

    public Collection(String title, ArrayList<SavedMovie> movies, String keyFirebase) {
        this.title = title;
        this.savedMoviesInTag = movies;
        this.keyFirebase = keyFirebase;
    }

    public Collection(String title, ArrayList<SavedMovie> movie) {
        this.title = title;
        this.savedMoviesInTag = movie;
    }

    public void addMovie(SavedMovie movie){
        this.savedMoviesInTag.add(movie);
    }

    public Collection(String title){
        this.title = title;
        savedMoviesInTag = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<SavedMovie> getSavedMoviesInTag() {
        return savedMoviesInTag;
    }

    public void setSavedMoviesInTag(ArrayList<SavedMovie> savedMoviesInTag) {
        this.savedMoviesInTag = savedMoviesInTag;
    }
}