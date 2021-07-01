package com.saikalyandaroju.appnews.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.saikalyandaroju.appnews.R;
import com.saikalyandaroju.appnews.Utils.NewsViewModelFactory;
import com.saikalyandaroju.appnews.repository.NewsRepository;
import com.saikalyandaroju.appnews.source.local.ArticleDatabase;
import com.saikalyandaroju.appnews.ui.viewmodel.NewsViewModel;

import static com.saikalyandaroju.appnews.Utils.MyApplication.isInternetAvailable;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NavController navController;

    public NewsViewModel newsViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppNews);

        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
       /* if (!isInternetAvailable()) {
            Snackbar snackbar = Snackbar.make(bottomNavigationView, "Check Internet Connection ", Snackbar.LENGTH_SHORT);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getRootView().getLayoutParams();
            params.setMargins(0, 0, 0, 35);

            snackbar.getView().setLayoutParams(params);

            snackbar.setAnchorView(bottomNavigationView);

            snackbar.show();

            return;
        }*/


        navController = Navigation.findNavController(this, R.id.newsNavHostFragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        setUp();

    }

    private void setUp() {

        NewsRepository newsRepository = new NewsRepository(ArticleDatabase.getDatabase(this));

        NewsViewModelFactory newsViewModelFactory = new NewsViewModelFactory(newsRepository);

        newsViewModel = new ViewModelProvider(this, newsViewModelFactory).get(NewsViewModel.class);
    }
}