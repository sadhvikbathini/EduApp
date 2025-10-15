package com.example.eduapp.model;

public class Batch {
    private String title;
    private int imageRes;    // or String imageUrl for remote images
    private String category; // e.g., "All","Offline","Mahapack"
    private boolean isNew;

    public Batch(String title, int imageRes, String category, boolean isNew) {
        this.title = title;
        this.imageRes = imageRes;
        this.category = category;
        this.isNew = isNew;
    }
    public String getTitle() { return title; }
    public int getImageRes() { return imageRes; }
    public String getCategory() { return category; }
    public boolean isNew() { return isNew; }
}
