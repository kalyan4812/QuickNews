package com.saikalyandaroju.appnews.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.saikalyandaroju.appnews.R;
import com.saikalyandaroju.appnews.Utils.RecyclerViewPaginator;
import com.saikalyandaroju.appnews.Utils.network.ApiResponse;
import com.saikalyandaroju.appnews.Utils.network.ResponseStatus;
import com.saikalyandaroju.appnews.adapters.NewsAdapter;
import com.saikalyandaroju.appnews.baseclasses.BaseFragment;
import com.saikalyandaroju.appnews.source.local.models.Article;
import com.saikalyandaroju.appnews.source.remote.models.NewsResponse;
import com.saikalyandaroju.appnews.ui.MainActivity;
import com.saikalyandaroju.appnews.ui.viewmodel.NewsViewModel;

import static com.saikalyandaroju.appnews.Utils.Constants.QUERY_PAGE_SIZE;


public class NewsFragment extends BaseFragment<NewsViewModel> {

    //TAG
    private static final String TAG = "NewsFragment";

    @Override
    public int getLayoutId() {
        return R.layout.fragment_news;
    }

    //Widgets
    ProgressBar progressBar;
    NavController navController;

    //Adapter
    NewsAdapter newsAdapter;

    //booleans for paging.
    boolean isLoading = false;
    boolean isScrolling = false;
    boolean isLastPage = false;
    RecyclerView recyclerView;


    @Override
    public void onViewReady(View view, Bundle savedStateInstance, Bundle arguments) {


        progressBar = view.findViewById(R.id.paginationProgressBar);
        navController = Navigation.findNavController(view);


        initRecyclerView(view);

        subscribeObservers();


        newsAdapter.setOnClickListener(new NewsAdapter.ClickListener() {
            @Override
            public void onClick(Article article) {
                Log.i(TAG, "Clicked");
                Bundle bundle = new Bundle();
                bundle.putSerializable("article", article);
                navController.navigate(R.id.action_newsFragment_to_articleFragment, bundle);

            }
        });


    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true;
            }

        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();

            boolean isNotLoadingAndNotLastPage = (!isLoading) && (!isLastPage);
            boolean isAtLastItem = (firstVisibleItemPosition + visibleItemCount) >= totalItemCount;
            boolean isNotBeginning = firstVisibleItemPosition >= 0;
            boolean isTotalMoreThanVisible = totalItemCount >= (QUERY_PAGE_SIZE);

            boolean shouldPaginate = (isNotLoadingAndNotLastPage) && (isAtLastItem) && (isNotBeginning) && (isTotalMoreThanVisible)
                    && (isScrolling);

            if (shouldPaginate) {
                getViewModel().getBreakingNews("IN");
                isScrolling = false;
            }
        }
    };

    private void subscribeObservers() {
        getViewModel().getNewsresponse().observe(getViewLifecycleOwner(), new Observer<ApiResponse<NewsResponse>>() {
            @Override
            public void onChanged(ApiResponse<NewsResponse> newsResponseApiResponse) {
                // Log.i("checking", newsResponseApiResponse.data.getArticles() + "");
                if (newsResponseApiResponse != null && newsResponseApiResponse.status == ResponseStatus.SUCCESS) {

                    newsAdapter.setList(newsResponseApiResponse.data.getArticles());
                    int totalpages = newsResponseApiResponse.data.getTotalResults() / QUERY_PAGE_SIZE + 2;
                    isLastPage = (getViewModel().breakingNewsPage == totalpages);
                    if (isLastPage) {
                        recyclerView.setPadding(0, 0, 0, 0);
                    }
                } else if (newsResponseApiResponse != null && newsResponseApiResponse.status == ResponseStatus.INTERNET_ISSUE) {
                    Snackbar.make(getView(), "Check Internet Connection", Snackbar.LENGTH_SHORT).show();

                } else if (newsResponseApiResponse != null && newsResponseApiResponse.status == ResponseStatus.ERROR) {

                    Toast.makeText(getContext(), newsResponseApiResponse.errorDescription, Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "Failed to load Data...");
                }
            }
        });

        getViewModel().loadingStatus.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                    isLoading = true;
                } else {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                }
            }
        });
    }

    private void initRecyclerView(View view) {
        newsAdapter = new NewsAdapter();
        recyclerView = view.findViewById(R.id.rvBreakingNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(newsAdapter);
        recyclerView.addOnScrollListener(scrollListener);

    }
}