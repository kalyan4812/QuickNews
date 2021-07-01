package com.saikalyandaroju.appnews.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saikalyandaroju.appnews.Utils.network.ApiResponse;
import com.saikalyandaroju.appnews.Utils.network.ResponseListener;
import com.saikalyandaroju.appnews.Utils.network.ResponseStatus;
import com.saikalyandaroju.appnews.Utils.network.SingleLiveEvent;
import com.saikalyandaroju.appnews.repository.NewsRepository;
import com.saikalyandaroju.appnews.source.local.models.Article;
import com.saikalyandaroju.appnews.source.remote.models.NewsResponse;

import java.util.List;

public class NewsViewModel extends ViewModel {


    private NewsRepository newsRepository;

    //NetworkSetUp
    private MutableLiveData<ApiResponse<NewsResponse>> newsresponse = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<NewsResponse>> searchNewsResponse = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<List<Article>>> savedarticles = new MutableLiveData<>();
    private LiveData<String> networkConnection=new MutableLiveData<>();

    public int breakingNewsPage = 1;
    public int serachNewsPage = 1;
    public SingleLiveEvent<Boolean> loadingStatus = new SingleLiveEvent<>();

    //Paging
    private ApiResponse<NewsResponse> pagingnewsresponse;
    private ApiResponse<NewsResponse> pagingsearchresponse;

    public NewsViewModel(NewsRepository newsRepository) { // to do constructur mechanism o f getting object we need cutsom viewmodelfactory.
        this.newsRepository = newsRepository;
        getBreakingNews("IN");
        getDbSavedArticles();
    }


    //Ask Repository for breakingNews.
    public void getBreakingNews(String country) {

        loadingStatus.postValue(true);
        Log.i("checking", breakingNewsPage + "");
        newsRepository.getBreakingNews(country, breakingNewsPage, new ResponseListener<NewsResponse>() {
            @Override
            public void onStart() {
                loadingStatus.postValue(true);
            }

            @Override
            public void onFinish() {
                loadingStatus.postValue(false);
            }

            @Override
            public void onResponse(ApiResponse<NewsResponse> apiResponse) {
                loadingStatus.postValue(false);
                breakingNewsPage++;
                if (apiResponse != null && apiResponse.status==ResponseStatus.SUCCESS) {

                    if (pagingnewsresponse == null) {
                        pagingnewsresponse = apiResponse;
                    } else {
                        //List<Article> oldarticles = pagingnewsresponse.data.getArticles();
                        List<Article> newarticles = apiResponse.data.getArticles();
                        pagingnewsresponse.data.getArticles().addAll(newarticles);

                        //Log.i("checking", pagingnewsresponse.data.getArticles().size() + "");
                    }
                    if (pagingnewsresponse == null) {
                        newsresponse.postValue(apiResponse);
                    } else {
                        newsresponse.postValue(pagingnewsresponse);
                       // Log.i("checking", pagingnewsresponse.data.getArticles().size() + "");
                    }

                }
                else{
                    newsresponse.postValue(apiResponse);
                }

            }
        });
    }

    //Ask Repository news for serachQuery
    public void searchForBreakingNews(String searchQuery) {
        loadingStatus.postValue(true);
        Log.i("checking",serachNewsPage+"");
        newsRepository.searchForBreakingNews(searchQuery, serachNewsPage, new ResponseListener<NewsResponse>() {
            @Override
            public void onStart() {
                loadingStatus.postValue(true);
            }

            @Override
            public void onFinish() {
                loadingStatus.postValue(false);
            }

            @Override
            public void onResponse(ApiResponse<NewsResponse> apiResponse) {
                loadingStatus.postValue(false);

                if (apiResponse != null && apiResponse.status==ResponseStatus.SUCCESS) {
                    if(serachNewsPage==1 && pagingsearchresponse!=null && pagingsearchresponse.data!=null){
                        pagingsearchresponse.data.getArticles().clear();
                    }
                    serachNewsPage++;
                    if (pagingsearchresponse == null) {
                        pagingsearchresponse = searchNewsResponse.getValue();
                    } else {
                        List<Article> newarticles = apiResponse.data.getArticles();
                        pagingsearchresponse.data.getArticles().addAll(newarticles);
                    }
                    if (pagingsearchresponse == null) {
                        searchNewsResponse.postValue(apiResponse);
                    } else {
                        searchNewsResponse.postValue(pagingsearchresponse);
                    }
                } else {
                    searchNewsResponse.postValue(apiResponse);
                }
            }
        });

    }

    public void insertArticle(Article article) {
        newsRepository.insertArticle(article, new ResponseListener<Long>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResponse(ApiResponse<Long> apiResponse) {

            }
        });
    }

    public void getDbSavedArticles() {
        loadingStatus.postValue(true);
        newsRepository.getSavedNews(new ResponseListener<List<Article>>() {
            @Override
            public void onStart() {
                loadingStatus.postValue(true);
            }

            @Override
            public void onFinish() {
                loadingStatus.postValue(false);
            }

            @Override
            public void onResponse(ApiResponse<List<Article>> apiResponse) {
                loadingStatus.postValue(false);
                if (apiResponse != null) {
                    savedarticles.postValue(apiResponse);
                }
            }
        });
    }

    public void deleteArticle(Article article) {
        newsRepository.deleteArticle(article, new ResponseListener<Integer>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResponse(ApiResponse<Integer> apiResponse) {

            }
        });
    }

    //Getters

    public MutableLiveData<ApiResponse<NewsResponse>> getSearchNewsResponse() {
        return searchNewsResponse;
    }

    public MutableLiveData<ApiResponse<List<Article>>> getSavedarticles() {
        return savedarticles;
    }

    public SingleLiveEvent<Boolean> getLoadingStatus() {
        return loadingStatus;
    }

    public MutableLiveData<ApiResponse<NewsResponse>> getNewsresponse() {
        return newsresponse;
    }
}
