package com.app.androidtest.repo.remote;

import com.app.androidtest.repo.remote.model.getCategoriesRes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RemoteRepo {
    public static String Base_url = "https://dietnesskw.com/api/user/";
    public static String domainGolabal = "https://dietnesskw.com/";
    private static RemoteRepo RemoteRepo;
    private ApiEndpoints endpoints;

    public RemoteRepo() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Long.class, new LongTypeAdapter())
                .registerTypeAdapter(String.class, new stringTypeAdapter())
                .create();
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(Base_url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        endpoints = adapter.create(ApiEndpoints.class);

    }

    public static RemoteRepo RemoteRepoInst() {
        if (RemoteRepo == null) {
            RemoteRepo = new RemoteRepo();
        }
        return RemoteRepo;
    }




    public Observable<getCategoriesRes> getCategories(String Language) {
        return endpoints.getCategories(Language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
