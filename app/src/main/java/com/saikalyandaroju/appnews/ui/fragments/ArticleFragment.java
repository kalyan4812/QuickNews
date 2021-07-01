package com.saikalyandaroju.appnews.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavArgs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.saikalyandaroju.appnews.R;
import com.saikalyandaroju.appnews.baseclasses.BaseFragment;
import com.saikalyandaroju.appnews.source.local.models.Article;
import com.saikalyandaroju.appnews.ui.viewmodel.NewsViewModel;


public class ArticleFragment extends BaseFragment<NewsViewModel> {


    //widgets
    WebView webView;
    FloatingActionButton saveArticle;

    Article article;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_article;
    }

    @Override
    public void onViewReady(View view, Bundle savedStateInstance, Bundle arguments) {

        webView = view.findViewById(R.id.webView);
        saveArticle = view.findViewById(R.id.fab);

        if (getArguments() != null) {

            article = ArticleFragmentArgs.fromBundle(getArguments()).getArticle();

            //setUPWebView
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.loadUrl(article.getUrl());
        }
        saveArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getViewModel().insertArticle(article);
                Snackbar.make(view,"Article Was Saved Succesfully",Snackbar.LENGTH_SHORT).show();
            }
        });


    }
}