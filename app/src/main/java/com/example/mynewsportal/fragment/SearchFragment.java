package com.example.mynewsportal.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.mynewsportal.R;
import com.google.android.material.textfield.TextInputLayout;


public class SearchFragment extends Fragment implements View.OnClickListener{

    Button btnSearch, btnEntrmnt, btnHealt, btnSci, btnSport, btnTech, btnBusins;
    EditText etSearchBerita;
    TextInputLayout searchLayout;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_search, container, false);

        etSearchBerita = v.findViewById(R.id.et_search_searchNews);
        searchLayout = v.findViewById(R.id.search_searchNewsLayout);
        btnSearch = v.findViewById(R.id.btn_search_search);
        btnEntrmnt = v.findViewById(R.id.btn_search_entertainment);
        btnHealt = v.findViewById(R.id.btn_search_health);
        btnSci = v.findViewById(R.id.btn_search_science);
        btnSport = v.findViewById(R.id.btn_search_sports);
        btnTech = v.findViewById(R.id.btn_search_technology);
        btnBusins = v.findViewById(R.id.btn_search_business);

        btnSearch.setOnClickListener(this);
        btnEntrmnt.setOnClickListener(this);
        btnHealt.setOnClickListener(this);
        btnSci.setOnClickListener(this);
        btnSport.setOnClickListener(this);
        btnTech.setOnClickListener(this);
        btnBusins.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        String keyword = etSearchBerita.getText().toString();
        switch (view.getId()){
            case R.id.btn_search_search:
                if (keyword.length() >= 3){
                    Navigation.findNavController(v)
                            .navigate(SearchFragmentDirections.actionSearchFragmentToListBeritaFragment(keyword,""));
                }else {
                    searchLayout.setError("Enter at least 3 characters");
                }
                break;
            case R.id.btn_search_entertainment:
                Navigation.findNavController(v).navigate(SearchFragmentDirections
                        .actionSearchFragmentToListBeritaFragment(keyword,btnEntrmnt.getText().toString()));
                break;
            case R.id.btn_search_sports:
                Navigation.findNavController(v).navigate(SearchFragmentDirections
                        .actionSearchFragmentToListBeritaFragment(keyword,btnSport.getText().toString()));
                break;
            case R.id.btn_search_science:
                Navigation.findNavController(v).navigate(SearchFragmentDirections
                        .actionSearchFragmentToListBeritaFragment(keyword,btnSci.getText().toString()));
                break;
            case R.id.btn_search_technology:
                Navigation.findNavController(v).navigate(SearchFragmentDirections
                        .actionSearchFragmentToListBeritaFragment(keyword,btnTech.getText().toString()));
                break;
            case R.id.btn_search_business:
                Navigation.findNavController(v).navigate(SearchFragmentDirections
                        .actionSearchFragmentToListBeritaFragment(keyword,btnBusins.getText().toString()));
                break;
            case R.id.btn_search_health:
                Navigation.findNavController(v).navigate(SearchFragmentDirections
                        .actionSearchFragmentToListBeritaFragment(keyword, btnHealt.getText().toString()));
                break;
        }
    }
}
