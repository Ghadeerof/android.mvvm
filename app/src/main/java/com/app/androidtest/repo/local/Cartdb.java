package com.app.androidtest.repo.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.app.androidtest.repo.remote.model.Product;

@Database(entities = {Product.class},version = 2)
@TypeConverters(Converters.class)
public abstract class Cartdb extends RoomDatabase {
    public abstract dbOperations dbOperations();
    private static Cartdb cartdb;

    public static Cartdb getCartdb(Context context){
        if (cartdb==null){
            cartdb= Room.databaseBuilder(context,Cartdb.class,"cartDb").allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build();
        }
        return cartdb;
    }
}
