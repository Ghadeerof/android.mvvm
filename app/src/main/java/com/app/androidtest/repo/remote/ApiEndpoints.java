package com.app.androidtest.repo.remote;

import com.app.androidtest.repo.remote.model.getCategoriesRes;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiEndpoints {

    @GET("categories/get")
    @Headers({"Accept: application/json"})
    Observable<getCategoriesRes> getCategories(@Header("Accept-Language") String Language);

}
