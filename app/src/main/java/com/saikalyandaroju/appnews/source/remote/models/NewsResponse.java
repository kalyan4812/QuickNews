package com.saikalyandaroju.appnews.source.remote.models;

import androidx.lifecycle.MutableLiveData;

import com.saikalyandaroju.appnews.source.local.models.Article;

import java.util.List;

public class NewsResponse {
    private List<Article> articles;
    private String status;
    private int totalResults;

    public NewsResponse(List<Article> articles, String status, int totalResults) {
        this.articles = articles;
        this.status = status;
        this.totalResults = totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
