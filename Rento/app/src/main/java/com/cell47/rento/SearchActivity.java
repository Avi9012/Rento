package com.cell47.rento;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    MaterialSearchBar materialSearchBar;
    List<String> suggestList = new ArrayList<>();
    int count = 0;
    private DatabaseReference dbref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        materialSearchBar = findViewById(R.id.searchcity);
        loadSuggestions();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setHint("Search for cities here");
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())) {
                        suggest.add(search);


                    }
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {
                onStart();
            }
        });


        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    closeKeyboard();
                }

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                //editor.putString("postalcode",model.getPostalcode());
                editor.putString("city",text.toString().toUpperCase());
                editor.apply();
                closeKeyboard();
                startActivity(new Intent(SearchActivity.this,HomeScreen.class));
                finish();
                count = 0;

            }

            @Override
            public void onButtonClicked(int buttonCode) {
                loadSuggestions();
            }
        });
    }
    private void closeKeyboard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), 0);

    }
    private void loadSuggestions() {
        dbref = FirebaseDatabase.getInstance().getReference().child("cname");
        dbref.orderByChild("city").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BlogCities blogMenu = postSnapshot.getValue(BlogCities.class);
                    assert blogMenu != null;
                    suggestList.add(blogMenu.getCity());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
