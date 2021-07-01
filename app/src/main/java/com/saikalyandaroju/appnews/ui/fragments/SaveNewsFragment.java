package com.saikalyandaroju.appnews.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.saikalyandaroju.appnews.R;
import com.saikalyandaroju.appnews.Utils.network.ApiResponse;
import com.saikalyandaroju.appnews.adapters.NewsAdapter;
import com.saikalyandaroju.appnews.baseclasses.BaseFragment;
import com.saikalyandaroju.appnews.source.local.models.Article;
import com.saikalyandaroju.appnews.ui.viewmodel.NewsViewModel;

import java.util.List;

public class SaveNewsFragment extends BaseFragment<NewsViewModel> {
    private static final String TAG = "SaveNewsFragment";


    //Widgets
    ProgressBar progressBar;
    NavController navController;

    //Adapter
    NewsAdapter newsAdapter;

    private ItemTouchHelper.SimpleCallback callback;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_save_news;
    }


    @Override
    public void onViewReady(View view, Bundle savedStateInstance, Bundle arguments) {

        progressBar = view.findViewById(R.id.paginationProgressBar);
        navController = Navigation.findNavController(view);

        initSwiper(view);
        initRecyclerView(view);
        newsAdapter.setOnClickListener(new NewsAdapter.ClickListener() {
            @Override
            public void onClick(Article article) {
                Log.i(TAG, "Clicked");
                Bundle bundle = new Bundle();
                bundle.putSerializable("article", article);
                navController.navigate(R.id.action_saveNewsFragment_to_articleFragment, bundle);

            }
        });

        subsrcibeToObservers();



    }

    private void initSwiper(View view) {
        callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAbsoluteAdapterPosition();
                Article article=newsAdapter.getList().get(pos);
                getViewModel().deleteArticle(article);

                Snackbar.make(view, "Deleted Successfully", Snackbar.LENGTH_SHORT).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      getViewModel().insertArticle(article);
                    }
                }).show();
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE){
                    View view=viewHolder.itemView;
                    Paint p=new Paint();
                    Bitmap bitmap;
                    //dx>0 swipe left to right
                    if(dX<0){
                        bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ic_delete_white_png);
                        p.setColor(Color.RED);
                        c.drawRect(view.getRight()+dX,view.getTop(),view.getRight(),view.getBottom(),p);
                        c.drawBitmap(bitmap,view.getRight()-bitmap.getWidth(),view.getTop()+(view.getBottom()-view.getTop()-bitmap.getHeight())/2 ,p);
                    }
                    else {
                        bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ic_delete_white_png);
                        p.setColor(Color.GREEN);
                        c.drawRect(view.getLeft(),view.getTop(),view.getLeft()+dX,view.getBottom(),p);
                        c.drawBitmap(bitmap,view.getLeft(),view.getTop()+(view.getBottom()-view.getTop()-bitmap.getHeight())/2 ,p);
                    }
                    viewHolder.itemView.setTranslationX(dX);;
                }
                else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

        };

    }


    private void subsrcibeToObservers() {
        getViewModel().getSavedarticles().observe(getViewLifecycleOwner(), new Observer<ApiResponse<List<Article>>>() {
            @Override
            public void onChanged(ApiResponse<List<Article>> listApiResponse) {
                Log.i("check",listApiResponse.data.toString());
                if (listApiResponse != null && listApiResponse.data != null) {
                    newsAdapter.setList(listApiResponse.data);
                }
            }
        });
    }

    private void initRecyclerView(View view) {
        newsAdapter = new NewsAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.rvSavedNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(newsAdapter);
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }
}