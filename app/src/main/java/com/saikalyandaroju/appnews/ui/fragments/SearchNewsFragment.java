package com.saikalyandaroju.appnews.ui.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.saikalyandaroju.appnews.R;
import com.saikalyandaroju.appnews.Utils.network.ApiResponse;
import com.saikalyandaroju.appnews.Utils.network.ResponseStatus;
import com.saikalyandaroju.appnews.adapters.NewsAdapter;
import com.saikalyandaroju.appnews.baseclasses.BaseFragment;
import com.saikalyandaroju.appnews.source.local.models.Article;
import com.saikalyandaroju.appnews.source.remote.models.NewsResponse;
import com.saikalyandaroju.appnews.ui.viewmodel.NewsViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.saikalyandaroju.appnews.Utils.Constants.QUERY_PAGE_SIZE;
import static com.saikalyandaroju.appnews.Utils.Constants.SEARCH_DELAY;

public class SearchNewsFragment extends BaseFragment<NewsViewModel> {

    private static final String TAG = "SearchNewsFragment";
    //Widgets
    ProgressBar progressBar;
    AppCompatEditText searchBox;

    //Adapter
    NewsAdapter newsAdapter;
    NavController navController;

    //booleans for paging.
    boolean isLoading = false;
    boolean isScrolling = false;
    boolean isLastPage = false;
    RecyclerView recyclerView;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search_news;
    }

    @Override
    public void onViewReady(View view, Bundle savedStateInstance, Bundle arguments) {

        progressBar = view.findViewById(R.id.paginationProgressBar);
        searchBox = view.findViewById(R.id.etSearch);
        navController = Navigation.findNavController(view);

        query(searchBox);

        initRecyclerView(view);

        subscribeObservers();

        newsAdapter.setOnClickListener(new NewsAdapter.ClickListener() {
            @Override
            public void onClick(Article article) {
                Log.i(TAG, "Clicked");
                Bundle bundle = new Bundle();
                bundle.putSerializable("article", article);
                navController.navigate(R.id.action_searchNewsFragment_to_articleFragment, bundle);

            }
        });
    }

    private void query(EditText searchBox) {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Exception {
                searchBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String text = charSequence.toString();
                        if (!emitter.isDisposed() && !text.isEmpty()) {
                            getViewModel().serachNewsPage = 1;

                            newsAdapter.setList(null);
                            emitter.onNext(text);

                        }


                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        }).debounce(SEARCH_DELAY, TimeUnit.MILLISECONDS);

        observable.filter(new Predicate<String>() {
            @Override
            public boolean test(@NonNull String text) throws Exception {
                if (text.isEmpty()) {

                    return false;
                } else {
                    return true;
                }

            }
        }).distinctUntilChanged().subscribe(new io.reactivex.Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.i("checking",s);
                getViewModel().searchForBreakingNews(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void subscribeObservers() {
        getViewModel().getSearchNewsResponse().observe(getViewLifecycleOwner(), new Observer<ApiResponse<NewsResponse>>() {
            @Override
            public void onChanged(ApiResponse<NewsResponse> newsResponseApiResponse) {
                if (newsResponseApiResponse != null && newsResponseApiResponse.status == ResponseStatus.SUCCESS) {

                    newsAdapter.setList(newsResponseApiResponse.data.getArticles());
                    int totalpages = newsResponseApiResponse.data.getTotalResults() / QUERY_PAGE_SIZE + 2;
                    isLastPage = (getViewModel().serachNewsPage == totalpages);
                    if (isLastPage) {
                        recyclerView.setPadding(0, 0, 0, 0);
                        getViewModel().serachNewsPage = 1;


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

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@androidx.annotation.NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true;
            }

        }

        @Override
        public void onScrolled(@androidx.annotation.NonNull RecyclerView recyclerView, int dx, int dy) {
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
                getViewModel().searchForBreakingNews(searchBox.getText().toString());
                isScrolling = false;
            }
        }
    };

    private void initRecyclerView(View view) {
        newsAdapter = new NewsAdapter();
        recyclerView = view.findViewById(R.id.rvSearchNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(newsAdapter);
        recyclerView.addOnScrollListener(scrollListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}