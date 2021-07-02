package com.app.androidtest.repo.local;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.app.androidtest.repo.remote.model.Product;

import java.util.List;

import io.reactivex.Flowable;


@Dao
public interface dbOperations {
    @Insert( onConflict= OnConflictStrategy.REPLACE)
    public void addProduct(List<Product> product);

    @Insert
    public Long addProduct(Product product);

    @Update
    public void updateProduct(Product product);

    @Delete
    public void deleteProduct(Product product);

    @Query("select * FROM Product where deleted =0")
    public Flowable<List<Product>> getProducts();

    @Query("select * FROM Product where deleted =1")
    public Flowable<List<Product>> getDeletedProducts();

    @Query("select * FROM Product where title LIKE '%' || :title || '%' and deleted =0")
    public Flowable<List<Product>> getProducts(String title);



}
