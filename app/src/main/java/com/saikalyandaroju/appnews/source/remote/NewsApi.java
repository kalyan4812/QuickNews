package com.saikalyandaroju.appnews.source.remote;

import com.saikalyandaroju.appnews.source.remote.models.NewsResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {

    @GET("v2/top-headlines")
    Observable<NewsResponse> getBreakingNews(

            @Query("country") String
                    countryCode,
            @Query("page") int
                    pageNumber,
            @Query("apiKey") String
                    apiKey
    );

    @GET("v2/everything")
    Observable<NewsResponse> searchForNews(

            @Query("q") String
                    searchQuery,
            @Query("page") int
                    pageNumber,
            @Query("apiKey") String
                    apiKey
    );

}
