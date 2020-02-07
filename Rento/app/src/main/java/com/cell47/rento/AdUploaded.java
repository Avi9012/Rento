package com.cell47.rento;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;

public class AdUploaded extends AppCompatActivity implements BaseSliderView.ImageLoadListener,ViewPagerEx.OnPageChangeListener {


    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    SliderLayout sliderLayout;
    HashMap<String, String> HashMapForURL;
    String random_string, loc, desc_, pr;
    TextView price,tagline,type,almirah,window,comment, location, description;
    private int flag;
    private Button book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_uploaded);
        prepareAd();
        //AdView adView = new AdView(this);
        //adView.setAdSize(AdSize.BANNER);
        //adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        // TODO: Add adView to your view hierarchy.
        mAdView = findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        flag= getIntent().getIntExtra("flag",99);
        sliderLayout=findViewById(R.id.Adslider);
        price=findViewById(R.id.rate);
        loc = "The room is located at ";
        desc_ = "There are ";
        location = findViewById(R.id.location);
        description = findViewById(R.id.desc);
        //tagline=findViewById(R.id.tvtagline);
        book=findViewById(R.id.bbook);
        //contact=findViewById(R.id.bcontact);

        if(flag==0){
            book.setVisibility(View.GONE);
            //contact.setVisibility(View.GONE);
        }

        type=findViewById(R.id.type);
        //almirah=findViewById(R.id.tvalmirah);
        //window=findViewById(R.id.tvwindows);
        comment=findViewById(R.id.comments);
        random_string=getIntent().getStringExtra("pushid");
        assert (random_string!=null);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(AdUploaded.this,Comment.class);
                i.putExtra("pushid",random_string);
                startActivity(i);
            }
        });
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        FirebaseDatabase.getInstance().getReference().child("ads").child(sharedPreferences.getString("city",null).toUpperCase())
                .child(random_string).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.child("pics").getChildren())
                    {
                        String name=snapshot.getValue().toString();

                        TextSliderView textSliderView = new TextSliderView(AdUploaded.this);

                        textSliderView

                                .image(name)
                                .setScaleType(BaseSliderView.ScaleType.Fit);

                        textSliderView.bundle(new Bundle());

                        textSliderView.getBundle()
                                .putString("extra",name);

                        sliderLayout.addSlider(textSliderView);
                    }
                    pr = dataSnapshot.child("price").getValue().toString();
                    price.setText("Rs."+dataSnapshot.child("price").getValue().toString());
                    book.setText("BooK Now in Rs. "+dataSnapshot.child("price").getValue().toString()+" Per Month.");
                    //tagline.setText(dataSnapshot.child("tagline").getValue().toString());
                    type.setText(dataSnapshot.child("type").getValue().toString());
                    desc_ = desc_+dataSnapshot.child("window").getValue().toString()+" windows for air circulation and "+dataSnapshot.child("almirah").getValue().toString()+" Almirahs are available in the room.";
                    description.setText((desc_));
                    loc = loc+dataSnapshot.child("address").getValue().toString()+", "+dataSnapshot.child("housenum").getValue().toString()+", "+dataSnapshot.child("landmark").getValue().toString()+", "+dataSnapshot.child("area").getValue().toString();
                    location.setText(loc);
                    //window.setText(dataSnapshot.child("window").getValue().toString()+" windows for air circulation");
                    //almirah.setText(dataSnapshot.child("almirah").getValue().toString()+" Almirahs available in the room");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sliderLayout.setDuration(3000);

        sliderLayout.addOnPageChangeListener(AdUploaded.this);

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(AdUploaded.this, BookingSelection.class);
                i.putExtra("fullamount", Integer.valueOf(pr));
                i.putExtra("pushid", random_string);
                startActivity(i);
            }
        });

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                mAdView.loadAd(adRequest);
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }

    private void prepareAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6950521191905333/3923338841");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onStart(BaseSliderView target) {

    }

    @Override
    public void onEnd(boolean result, BaseSliderView target) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void onBackPressed()
    {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new AdListener(){
                @Override
                public void onAdClosed() {
                    // Code to be executed when the interstitial ad is closed.
                    super.onAdClosed();
                    finish();
                }
            });
        }
        super.onBackPressed();
    }
}
