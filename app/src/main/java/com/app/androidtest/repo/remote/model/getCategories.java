package com.app.androidtest.repo.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class getCategories {
    @SerializedName("categories")
    private List<CategoryProducts> categories;
    @SerializedName("image_url")
    private String imageURL;

    public List<CategoryProducts> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryProducts> categories) {
        this.categories = categories;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
