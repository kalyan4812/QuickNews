package com.saikalyandaroju.appnews.Utils.network;

public interface ResponseListener<T> {
    void onStart();

    void onFinish();

    void onResponse(ApiResponse<T> apiResponse);
}
