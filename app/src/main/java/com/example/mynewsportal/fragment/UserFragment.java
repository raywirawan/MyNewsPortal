package com.example.mynewsportal.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.mynewsportal.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        //load saved settings
        String savedLang = sharedPref.getString("language", "English");
        Log.d(TAG, "savedPrefLang: "+savedLang);
        String[] Languages = new String[] {"English", "French", "Español", "German", "Italian", "Dutch"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        R.layout.dropdown_menu_popup_item,
                        Languages);

        AutoCompleteTextView editCountry = v.findViewById(R.id.tv_user_language);
        editCountry.setText(savedLang);
        editCountry.setAdapter(adapter);

        editCountry.setOnItemClickListener((adapterView, view, i, l) -> {
            String language = editCountry.getText().toString();
            SharedPreferences.Editor preferencesEditor = sharedPref.edit();
            preferencesEditor.putString("language", language);
            preferencesEditor.apply();
            Toast.makeText(getContext(), "Settings Saved", Toast.LENGTH_SHORT).show();
        });

        return v;
    }

}
