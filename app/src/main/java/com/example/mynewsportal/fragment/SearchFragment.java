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


public class SearchFragment extends Fragment {

    Button btnSearch;
    EditText etSearchBerita;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        etSearchBerita = v.findViewById(R.id.et_search_searchNews);
        btnSearch = v.findViewById(R.id.btn_search_search);

        etSearchBerita = v.findViewById(R.id.et_search_searchNews);
        btnSearch.setOnClickListener(view ->{
            String keyword = etSearchBerita.getText().toString();
            if (keyword.length() >= 3){
                Navigation.findNavController(v).navigate(SearchFragmentDirections.actionSearchFragmentToListBeritaFragment(keyword));
            }else {
                etSearchBerita.setError("Enter at least 3 characters");
            }
        });

        return v;
    }
}
