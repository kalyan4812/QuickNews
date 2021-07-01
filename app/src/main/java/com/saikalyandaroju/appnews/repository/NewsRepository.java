package com.saikalyandaroju.appnews.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.saikalyandaroju.appnews.Utils.network.ApiResponse;
import com.saikalyandaroju.appnews.Utils.network.ResponseListener;
import com.saikalyandaroju.appnews.Utils.network.ResponseStatus;
import com.saikalyandaroju.appnews.source.local.ArticleDatabase;
import com.saikalyandaroju.appnews.source.local.daos.ArticleDao;
import com.saikalyandaroju.appnews.source.local.models.Article;
import com.saikalyandaroju.appnews.source.remote.ServiceProvider;
import com.saikalyandaroju.appnews.source.remote.models.NewsResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.saikalyandaroju.appnews.BuildConfig.API_KEY;
import static com.saikalyandaroju.appnews.Utils.MyApplication.isInternetAvailable;

public class NewsRepository {
    private ArticleDatabase articleDatabase;

    public NewsRepository(ArticleDatabase articleDatabase) {
        this.articleDatabase = articleDatabase;
    }

    //Network Opeartions.
    public void getBreakingNews(String countryCode, int pagenumber, ResponseListener<NewsResponse> responseListener) {
        if(isInternetAvailable()) {
            performRequest(ServiceProvider.getNewsApi().getBreakingNews(countryCode, pagenumber, API_KEY), responseListener);
        }else{

            responseListener.onResponse(new ApiResponse(ResponseStatus.INTERNET_ISSUE, "No Internet", null));
        }

    }

    public void searchForBreakingNews(String searchQuery, int pagenumber, ResponseListener<NewsResponse> responseListener) {
        if(isInternetAvailable()) {
            performRequest(ServiceProvider.getNewsApi().searchForNews(searchQuery, pagenumber, API_KEY)
                    , responseListener);
        }
        else{
            responseListener.onResponse(new ApiResponse(ResponseStatus.INTERNET_ISSUE, null, null));
        }

    }

    //Db Operations
    public void insertArticle(Article article, ResponseListener<Long> responseListener) {
        performRequest(articleDatabase.getArticleDao().insertArticle(article).toObservable(), responseListener);
    }

    public void getSavedNews(ResponseListener<List<Article>> responseListener) {
        performRequest(articleDatabase.getArticleDao().getAllArticles().toObservable(), responseListener);
    }
    public void deleteArticle(Article article, ResponseListener<Integer> responseListener){
        performRequest(articleDatabase.getArticleDao().deleteArticle(article).toObservable(),responseListener);
    }


    private <T> void performRequest(Observable<T> observable, ResponseListener<T> responseListener) {
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                               @Override
                               public void onSubscribe(@NonNull Disposable d) {

                               }

                               @Override
                               public void onNext(@NonNull T t) {
                                   Log.i("check",t.toString());
                                   responseListener.onResponse(new ApiResponse(ResponseStatus.SUCCESS, t, null));

                               }

                               @Override
                               public void onError(@NonNull Throwable e) {
                                   responseListener.onResponse(new ApiResponse<>(ResponseStatus.ERROR, null, e));
                                   Log.i("check", e.getMessage());
                               }

                               @Override
                               public void onComplete() {
                                   responseListener.onFinish();
                               }
                           }
                );

    }


}
