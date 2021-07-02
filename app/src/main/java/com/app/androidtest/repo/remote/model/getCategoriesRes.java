package com.app.androidtest.repo.remote.model;

import com.google.gson.annotations.SerializedName;

public class getCategoriesRes {
    @SerializedName("error_flag")
    private long errorFlag;
    @SerializedName("message")
    private String message;
    @SerializedName("result")
    private getCategories result;

    public long getErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(long errorFlag) {
        this.errorFlag = errorFlag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public getCategories getResult() {
        return result;
    }

    public void setResult(getCategories result) {
        this.result = result;
    }
}
