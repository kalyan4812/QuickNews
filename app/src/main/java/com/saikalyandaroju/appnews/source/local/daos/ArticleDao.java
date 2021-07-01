package com.saikalyandaroju.appnews.source.local.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.saikalyandaroju.appnews.source.local.models.Article;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insertArticle(Article article);

    @Delete
    Single<Integer> deleteArticle(Article article);

    @Query("SELECT * FROM articles")
    Flowable<List<Article>> getAllArticles();
}
