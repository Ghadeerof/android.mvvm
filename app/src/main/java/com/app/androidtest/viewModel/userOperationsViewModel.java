package com.app.androidtest.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.androidtest.AppSession;
import com.app.androidtest.repo.local.Cartdb;
import com.app.androidtest.repo.remote.model.Product;
import com.app.androidtest.repo.remote.RemoteRepo;
import com.app.androidtest.repo.remote.model.getCategoriesRes;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.HttpException;


public class userOperationsViewModel extends ViewModel {

    public MutableLiveData<getCategoriesRes> getCategoriesRes = new MutableLiveData<>();
    public MutableLiveData<List<Product>> getProductsRes = new MutableLiveData<>();

    public void getCategories() {
        RemoteRepo.RemoteRepoInst().getCategories("")
                .subscribe(resluts -> getCategoriesRes.setValue(resluts), throwable -> {
                    throwable.printStackTrace();
                    if (throwable instanceof HttpException) {
                        ResponseBody errorBody = ((HttpException) throwable).response().errorBody();
                        getCategoriesRes.postValue(new Gson().fromJson(errorBody.string(), getCategoriesRes.class));
                    }
                });
    }

    public void addProducts(Cartdb dataSource,List<Product> product ){
        dataSource.dbOperations().addProduct(product);
    }

    public void addProduct(Cartdb dataSource,Product product ){
        dataSource.dbOperations().addProduct(product);
    }

    public void updateProducts(Cartdb dataSource,Product product ){
        dataSource.dbOperations().updateProduct(product);
    }

    public void deleteProduct(Cartdb dataSource,Product product ){
        dataSource.dbOperations().deleteProduct(product);
    }

    public void getProducts(Cartdb dataSource){
        dataSource.dbOperations().getProducts().subscribe(products ->getProductsRes.postValue(products));
    }

    public void getDeletedProducts(Cartdb dataSource){
        dataSource.dbOperations().getDeletedProducts().subscribe(products ->getProductsRes.postValue(products));
    }

    public void getProductsQuery(Cartdb dataSource,String query){
        dataSource.dbOperations().getProducts(query).subscribe(products ->getProductsRes.postValue(products));
    }


}
