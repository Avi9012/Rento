package com.cell47.rento;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

public class BookNow extends AppCompatActivity implements PaymentResultListener{

    int payamount;
    SharedPreferences sharedPreferences;
    String random_String;
    TextView tvname,tvadd,tvlandmark,tvcontact,tvprice,tvhousenum;
    Button call,shareonwhatsapp;
    DatabaseReference saveref;
    ActionBar actionBar;
    Button bmaps, book;
    boolean open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_now);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Contact Details ");

        random_String=getIntent().getStringExtra("pushid");
        tvadd=findViewById(R.id.tvaddress);
        tvcontact=findViewById(R.id.tvcontact);
        tvprice=findViewById(R.id.tvpricesave);
        tvlandmark=findViewById(R.id.tvlandmark);
        tvname=findViewById(R.id.tvnameowner);
        tvhousenum=findViewById(R.id.tvhousenum);
        call=findViewById(R.id.bcallnow);
        bmaps=findViewById(R.id.bgmaps);
        shareonwhatsapp=findViewById(R.id.bwhatsapp);
        book = findViewById(R.id.booknow);

        open = getIntent().getBooleanExtra("open", false);
        payamount=getIntent().getIntExtra("fullamount",100000);
        payamount=payamount*100;
        startPayment(String.valueOf(payamount));
    }

    private void startPayment(String amount) {
        payamount=1000;
        Checkout checkout=new Checkout();
        final Activity activity=this;
        try{
            JSONObject options=new JSONObject();
            options.put("description","Rento Room Booking for "+sharedPreferences.getString("phone",null));
            options.put("currency","INR");
            options.put("amount",amount);

            checkout.open(activity,options);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Successful..Loading Details ", Toast.LENGTH_SHORT).show();
        loadData();
    }

    private void loadData() {
        final SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("ads").child(sharedPreferences.getString("city",null).toUpperCase())
                .child(random_String).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String contact=dataSnapshot.child("contact").getValue(String.class);
                final String name=dataSnapshot.child("name").getValue(String.class);
                final String landmark=dataSnapshot.child("landmark").getValue(String.class);
                final String housenum=dataSnapshot.child("housenum").getValue(String.class);
                final String address=dataSnapshot.child("address").getValue(String.class);
                String price=dataSnapshot.child("price").getValue(String.class);
                final Double lat,lng;
                lat=dataSnapshot.child("latitude").getValue(Double.class);
                lng=dataSnapshot.child("lonitude").getValue(Double.class);
                bmaps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String latitude = String.valueOf(lat);
                        String longitude = String.valueOf(lng);
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        try{
                            startActivity(mapIntent);

                        }catch (NullPointerException e){
                        }
                    }
                });


                saveref=FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                        .child("savedads").push();
                saveref.child("name").setValue(name+"");
                saveref.child("address").setValue(housenum+address+landmark+"");
                saveref.child("contact").setValue(contact+"");
                saveref.child("latitude").setValue(lat);
                saveref.child("longitude").setValue(lng);

                tvadd.setText("Address: "+address+"");
                tvcontact.setText(contact+"");
                tvname.setText(name+"");
                tvlandmark.setText("Landmark : "+landmark+"");
                tvhousenum.setText("House Number: "+housenum+"");
                tvprice.setText("Rs."+price);

                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact));
                        startActivity(intent);
                    }
                });

                shareonwhatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            //Uri imgUri = Uri.parse(currentFile.getAbsolutePath());
                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            whatsappIntent.setType("text/plain");
                            whatsappIntent.setPackage("com.whatsapp");
                            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "I have found a room for rent \n"+"Address: "+housenum+"\n"
                            +address+"\n"+landmark+"\n"+"Contact :"+"\n"+name+" "+contact);

                            try {
                                startActivity(whatsappIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(BookNow.this, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed ", Toast.LENGTH_SHORT).show();
        finish();
    }
}
