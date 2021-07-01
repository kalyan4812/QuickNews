package com.saikalyandaroju.appnews.baseclasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.saikalyandaroju.appnews.Utils.NewsViewModelFactory;
import com.saikalyandaroju.appnews.repository.NewsRepository;
import com.saikalyandaroju.appnews.source.local.ArticleDatabase;
import com.saikalyandaroju.appnews.ui.viewmodel.NewsViewModel;

import static com.saikalyandaroju.appnews.Utils.MyApplication.isInternetAvailable;

public abstract class BaseFragment<V extends ViewModel> extends Fragment {
    private V viewmodel;

    public abstract @LayoutRes
    int getLayoutId();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performDependencyInjection();
    }

    protected  void performDependencyInjection(){

            NewsRepository newsRepository = new NewsRepository(ArticleDatabase.getDatabase(getContext()));

            NewsViewModelFactory newsViewModelFactory = new NewsViewModelFactory(newsRepository);

            viewmodel = (V) new ViewModelProvider(requireActivity(), newsViewModelFactory).get(NewsViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewReady(view,savedInstanceState, getArguments());
    }

    public V getViewModel(){

        return viewmodel;

    }

   public abstract void onViewReady(View view, Bundle savedStateInstance,Bundle arguments);
}
/*public abstract class BaseFragment<V extends BaseViewModel> extends Fragment {
    @Inject
    private V viewmodel;



    public abstract @LayoutRes
    int getLayoutId();

    public V getViewModels(){
        return viewmodel;

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpObservers();
        performDependencyInjection();

        viewmodel.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView(view);
    }

    protected void setUpObservers() {
        viewmodel.messageString.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                showStringMessage(s);
            }
        });

        viewmodel.messageStringId.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                showStringIdMessage(integer);
            }
        });
    }

    private void showStringIdMessage(@StringRes Integer integer) {
        Toast.makeText(getContext(), integer + "", Toast.LENGTH_SHORT).show();
    }

    private void showStringMessage(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    protected abstract void setUpView(View view);


    private void performDependencyInjection() {
        AndroidInjection.inject(getActivity());
    }*/

