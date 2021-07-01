package com.saikalyandaroju.appnews.source.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.saikalyandaroju.appnews.source.local.convertors.Convertor;
import com.saikalyandaroju.appnews.source.local.daos.ArticleDao;
import com.saikalyandaroju.appnews.source.local.models.Article;

@Database(entities = Article.class, version = 1)
@TypeConverters(Convertor.class)
public abstract class ArticleDatabase extends RoomDatabase {

    public abstract ArticleDao getArticleDao();

    private static ArticleDatabase articleDatabase;

    public static ArticleDatabase getDatabase(final Context context) {

        if (articleDatabase == null) {

            synchronized (ArticleDatabase.class) {

                if (articleDatabase == null) {

                    articleDatabase = Room.databaseBuilder(
                            context, ArticleDatabase.class, "ARTICLE_DATABASE")
                            .fallbackToDestructiveMigration()
                            .build();

                }

            }

        }

        return articleDatabase;

    }
}
