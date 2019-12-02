package com.example.mynewsportal.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mynewsportal.R;
import com.example.mynewsportal.models.Article;
import com.example.mynewsportal.utils.MyUtils;

import java.util.ArrayList;

public class HorizontalBeritaAdapter extends RecyclerView.Adapter<HorizontalBeritaAdapter.HorizontalBeritaViewHolder>{

    private ArrayList<Article> mData = new ArrayList<>();

    //set Callback to Fragment
    private OnItemClickCallback onItemClickCallback;
    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public void setData(ArrayList<Article> items) {
        mData.clear();
        mData.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HorizontalBeritaAdapter.HorizontalBeritaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_news_grid, parent, false);
        return new HorizontalBeritaViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalBeritaAdapter.HorizontalBeritaViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class HorizontalBeritaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSumber, textViewJudul, textViewTanggal;
        ImageView imageGambar;

        public HorizontalBeritaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJudul = itemView.findViewById(R.id.item_card_news_judul);
            textViewSumber = itemView.findViewById(R.id.item_card_news_sumber);
            textViewTanggal = itemView.findViewById(R.id.item_card_news_tanggal);
            imageGambar = itemView.findViewById(R.id.item_card_news_gambar);

        }
        @SuppressLint("CheckResult")
        void bind(Article article){
            String tanggal = MyUtils.getTanggalFormat(article.getPublishedAt()) + " | " + MyUtils.getJamFormat(article.getPublishedAt());
            textViewJudul.setText(article.getTitle());
            textViewTanggal.setText(tanggal);
            textViewSumber.setText(article.getName());
            Glide.with(itemView.getContext())
                    .load(article.getUrlToImage())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .into(imageGambar);
            itemView.setOnClickListener(view -> {
                onItemClickCallback.onItemClicked(article);
            });
        }
    }
    public interface OnItemClickCallback {
        void onItemClicked(Article article);
    }
}
