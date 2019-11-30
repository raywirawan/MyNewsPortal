package com.example.mynewsportal.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mynewsportal.activity.NewsDetailActivity;
import com.example.mynewsportal.R;
import com.example.mynewsportal.fragment.SearchFragment;
import com.example.mynewsportal.fragment.SearchFragmentDirections;
import com.example.mynewsportal.models.Article;
import com.example.mynewsportal.utils.MyUtils;

import java.util.ArrayList;

public class ListBeritaAdapter extends RecyclerView.Adapter<ListBeritaAdapter.ListBeritaViewHolder> {

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
    public ListBeritaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_news, parent, false);
        return new ListBeritaViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListBeritaViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ListBeritaViewHolder extends RecyclerView.ViewHolder{
        TextView textViewSumber, textViewJudul, textViewTanggal;
        ImageView imageGambar;

        public ListBeritaViewHolder(@NonNull View itemView) {
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
                    .apply(new RequestOptions().placeholder(R.drawable.ic_insert_photo_black_24dp))
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
