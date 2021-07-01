package com.saikalyandaroju.appnews.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.saikalyandaroju.appnews.R;
import com.saikalyandaroju.appnews.source.local.models.Article;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private ClickListener clickListener;

    //DiffUtil calculates differrnces b/w old list and new list ,only updates items which are changed.
    //And it also happen in background so no blockage of main thread.

    private DiffUtil.ItemCallback<Article> callback = new DiffUtil.ItemCallback<Article>() {
        @Override
        public boolean areItemsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getUrl() == newItem.getUrl(); // urls are unique
        }

        @Override
        public boolean areContentsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem==newItem;
        }
    };
    private AsyncListDiffer<Article> differ = new AsyncListDiffer<Article>(this, callback); // runs asynchronously.

    public void setList(List<Article> articles) {
        differ.submitList(articles);
    }

    public List<Article> getList() {
        return differ.getCurrentList();
    }


    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Article article = differ.getCurrentList().get(position);
        if (article != null) {
            Glide.with(holder.itemView.getContext()).load(article.getUrlToImage()).
                    placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(holder.articleImage);
            holder.title.setText(article.getTitle());
           
            holder.description.setText(article.getDescription());
            if (article.getSource() != null) {
                holder.source.setText(article.getSource().getName());
            }

            String time =article.getPublishedAt().replace("T", "");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));

            try {
                //Date date=new Date(top.getcreated());

                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("IST"));
                Date currentdate = c.getTime();
                Date date = simpleDateFormat.parse(time);

                Log.i("date", date.toString());
                holder.publsihedAt.setText(getDate(date));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        return simpleDateFormat.format(date);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView articleImage;
        MaterialTextView source, title, description, publsihedAt;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            articleImage = itemView.findViewById(R.id.ivArticleImage);
            source = itemView.findViewById(R.id.tvSource);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            publsihedAt = itemView.findViewById(R.id.tvPublishedAt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    if (clickListener != null && pos != -1) {
                        clickListener.onClick(differ.getCurrentList().get(pos));
                    }
                }
            });

        }
    }

    public interface ClickListener {
        void onClick(Article article);
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
