package com.saikalyandaroju.appnews.source.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.saikalyandaroju.appnews.Utils.Constants.BASE_URL;

public class ServiceProvider {

  static HttpLoggingInterceptor httpLoggingInterceptor=new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
  static OkHttpClient okHttpClient=new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();


    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static NewsApi requestApi = retrofit.create(NewsApi.class);

    public static NewsApi getNewsApi() {
        return requestApi;
    }
}
