package com.cell47.rento;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TermsAndConditions extends AppCompatActivity {

    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Terms and Conditions");

        Uri uri = Uri.parse("https://drive.google.com/file/d/1zYTVn0TTMpv1C6vHRhNnFvc3zcpqQkg5/view?usp=sharing");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        finish();
    }
}
