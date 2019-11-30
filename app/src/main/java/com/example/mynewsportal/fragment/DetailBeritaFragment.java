package com.example.mynewsportal.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mynewsportal.R;
import com.example.mynewsportal.models.Article;
import com.example.mynewsportal.utils.MyUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailBeritaFragment extends Fragment {
    private TextView tvJudul, tvSumber, tvPenulis, tvWaktu, tvKonten;
    private ImageView ivGambarBerita;

    public DetailBeritaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail_berita, container, false);


        tvJudul = v.findViewById(R.id.tv_newsDetail_judulBerita);
        tvPenulis = v.findViewById(R.id.tv_newsDetail_penulis);
        tvKonten = v.findViewById(R.id.tv_newsDetail_contentBerita);
        tvWaktu = v.findViewById(R.id.tv_newsDetail_waktuTerbit);
        tvSumber = v.findViewById(R.id.tv_newsDetail_sumberBerita);
        ivGambarBerita = v.findViewById(R.id.iv_newsDetail_gambarBerita);

        Article article = getArguments().getParcelable("article");
        assert article != null;
        tvJudul.setText(article.getTitle());
        tvSumber.setText(article.getName());
        String waktu = MyUtils.getTanggalFormat(article.getPublishedAt())+" | "+MyUtils.getJamFormat(article.getPublishedAt());
        tvWaktu.setText(waktu);
        tvKonten.setText(article.getContent());
        tvPenulis.setText(article.getAuthor());
        if (article.getUrlToImage()!=null){
            Glide.with(this).load(article.getUrlToImage())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_insert_photo_black_24dp))
                    .into(ivGambarBerita);
        } else {
            ivGambarBerita.setVisibility(View.GONE);
        }

        return v;
    }

}
