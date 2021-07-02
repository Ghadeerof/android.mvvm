package com.app.androidtest.repo.local;

import androidx.room.TypeConverter;

import com.app.androidtest.repo.remote.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {

    @TypeConverter
    public List<Product> fromString(String value) {
        Type listType = new TypeToken<List<Product>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public String fromArrayList(List<Product> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }


}
