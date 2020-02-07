package com.cell47.rento;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BookingSelection extends AppCompatActivity {


    Button payten,payfull,contactus;
    int fullamount;
    String random_string;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_selection);

        payten=findViewById(R.id.bpayten);
        payfull=findViewById(R.id.bpayfull);
        payfull.setVisibility(View.GONE);
        contactus=findViewById(R.id.bcontactus);
        random_string=getIntent().getStringExtra("pushid");

        fullamount=getIntent().getIntExtra("fullamount",100000);
        if (fullamount!=100000){
            payfull.setVisibility(View.VISIBLE);
            payfull.setText(payfull.getText()+" ( Rs."+fullamount+" )");
        }

        payfull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BookNow.class)
                        .putExtra("fullamount",fullamount)
                .putExtra("pushid",random_string));
            }
        });

        payten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BookNow.class)
                .putExtra("fullamount",10)
                .putExtra("pushid",random_string)
                .putExtra("open", true));
            }
        });

        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ContactDetails.class)
                .putExtra("pushid",random_string));
            }
        });
    }
}
