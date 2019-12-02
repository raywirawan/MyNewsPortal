package com.example.mynewsportal.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mynewsportal.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AddFollowActivity extends AppCompatActivity {

    private static final String TAG = "AddFollowActivity";
    private String sharedPrefFile = "com.mynewsportal.preferences";
    EditText[] editTexts;
    Button btnAdd, btnSave, btnDelete;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_follow);

        SharedPreferences sharedPref = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE);
        Set<String> followingSet = sharedPref.getStringSet("following", new HashSet<String>());
        ArrayList<String> follow = new ArrayList<>(followingSet);
//        preferencesEditor.putStringSet("following",following);
//        preferencesEditor.apply();
        editTexts = new EditText[5];
        editTexts[0] = findViewById(R.id.et_addfollow);
        editTexts[1] = findViewById(R.id.et_addfollow1);
        editTexts[2] = findViewById(R.id.et_addfollow2);
        editTexts[3] = findViewById(R.id.et_addfollow3);
        editTexts[4] = findViewById(R.id.et_addfollow4);
        i = 0;
        for (int j = 0; j < editTexts.length; j++) {
            editTexts[j].setVisibility(View.GONE);
        }
        Log.d(TAG, "onCreate: size2 "+followingSet.size());
        Log.d(TAG, "onCreate: size"+follow.size());
        if (follow.size() > 0){

            for (int j = 0; j < follow.size(); j++) {
                editTexts[j].setVisibility(View.VISIBLE);
                editTexts[j].setText(follow.get(j));
                editTexts[j].setEnabled(false);
                add();
            }
        }

        btnAdd = findViewById(R.id.btn_addfollow);
        btnAdd.setOnClickListener(view -> {
            if (i > 0){
                if (editTexts[i-1].getText().toString().length() < 3){
                    editTexts[i-1].setError("Minimum 3 characters");
                } else if (i < 5){
                    editTexts[i].setVisibility(View.VISIBLE);
                    editTexts[i-1].setEnabled(false);
                    Log.d(TAG, "var =  "+i);
                    add();
                }
            } else if(i==0){
                editTexts[i].setVisibility(View.VISIBLE);
                add();
            }
        });
        btnSave = findViewById(R.id.btn_saveFollow);
        btnSave.setOnClickListener(view -> {
            if (editTexts[i-1].getText().toString().length() < 3){
                editTexts[i-1].setError("Minimum 3 characters");
            } else{
                for (int j = 0; j <= i; j++) {
                    SharedPreferences.Editor preferencesEditor = sharedPref.edit();
                    Set<String> newFollowingSet = new HashSet<>(sharedPref.getStringSet("following", new HashSet<>()));
                    newFollowingSet.add(editTexts[i-1].getText().toString());

                    preferencesEditor.putStringSet("following", newFollowingSet);
                    preferencesEditor.apply();
                    onBackPressed();
                    Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show();
                }
            }

        });
        btnDelete = findViewById(R.id.btn_deletefollow);
        btnDelete.setOnClickListener(view -> {
            SharedPreferences.Editor preferencesEditor = sharedPref.edit();
            Set<String> emptyset = new HashSet<>();
            preferencesEditor.putStringSet("following", emptyset);
            preferencesEditor.apply();
            onBackPressed();
            Toast.makeText(this, "Data Cleared", Toast.LENGTH_SHORT).show();
        });

    }

    private void add(){
        if (i < 5) ++i;
        if (i==5){
            Toast.makeText(this, "You can follow maximum of 5 topics", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "i : "+i);
    }

}
