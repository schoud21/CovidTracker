package com.example.covidtracker.ui.dashboard;

public class SymptomsModel {

    private String name;
    private float rating;

    public SymptomsModel() {
    }

    public SymptomsModel(String name, float rating) {
        this.name = name;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
