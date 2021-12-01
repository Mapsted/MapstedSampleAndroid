package com.mapsted.sample;

import androidx.annotation.NonNull;

public class MyCategory {
    public String catId;
    public String title;
    public String imageUrl;

    public MyCategory(String catId, String title, String imageUrl) {
        this.catId = catId;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "title=" + title + ", imageurl=" + imageUrl;
    }
}