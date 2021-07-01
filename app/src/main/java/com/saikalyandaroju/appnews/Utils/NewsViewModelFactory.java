package com.saikalyandaroju.appnews.Utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.saikalyandaroju.appnews.repository.NewsRepository;
import com.saikalyandaroju.appnews.ui.viewmodel.NewsViewModel;

public class NewsViewModelFactory implements ViewModelProvider.Factory {

    private NewsRepository newsRepository;

    public NewsViewModelFactory(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewsViewModel(newsRepository);
    }
}
