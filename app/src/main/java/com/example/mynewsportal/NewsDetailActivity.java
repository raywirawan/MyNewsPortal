package com.example.mynewsportal;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mynewsportal.models.Article;
import com.example.mynewsportal.utils.MyUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsDetailActivity extends AppCompatActivity {

    private TextView tvJudul, tvSumber, tvPenulis, tvWaktu, tvKonten;
    private ImageView ivGambarBerita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Article article = getIntent().getParcelableExtra("article");

        tvJudul = findViewById(R.id.tv_newsDetail_judulBerita);
        tvPenulis = findViewById(R.id.tv_newsDetail_penulis);
        tvKonten = findViewById(R.id.tv_newsDetail_contentBerita);
        tvWaktu = findViewById(R.id.tv_newsDetail_waktuTerbit);
        tvSumber = findViewById(R.id.tv_newsDetail_sumberBerita);
        ivGambarBerita = findViewById(R.id.iv_newsDetail_gambarBerita);

        assert article != null;
        tvJudul.setText(article.getTitle());
        tvSumber.setText(article.getName());
        tvWaktu.setText(MyUtils.getTanggalFormat(article.getPublishedAt())+" | "+MyUtils.getJamFormat(article.getPublishedAt()));
        tvKonten.setText(article.getContent());
        tvPenulis.setText(article.getAuthor());
        if (article.getUrlToImage()!=null){
            Glide.with(this).load(article.getUrlToImage())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_insert_photo_black_24dp))
                    .into(ivGambarBerita);
        } else {
            ivGambarBerita.setVisibility(View.GONE);
        }

    }

}
