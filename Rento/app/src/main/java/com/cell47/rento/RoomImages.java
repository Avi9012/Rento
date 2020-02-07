package com.cell47.rento;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RoomImages extends AppCompatActivity implements BaseSliderView.ImageLoadListener,ViewPagerEx.OnPageChangeListener{

    String random_string;
    SliderLayout sliderLayout;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_images);
        random_string=getIntent().getStringExtra("random_string");
        actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        sliderLayout=findViewById(R.id.sliderLayout);

        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        FirebaseDatabase.getInstance().getReference().child("ads").child(sharedPreferences.getString("city",null).toUpperCase())
                .child(random_string).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    //String thumbnail=dataSnapshot.child("thumbnail").getValue(String.class);

                            for(DataSnapshot snapshot:dataSnapshot.child("pics").getChildren())

                            {
                                String name=snapshot.getValue().toString();

                                TextSliderView textSliderView = new TextSliderView(RoomImages.this);

                                textSliderView
                                        .image(name)
                                        .setScaleType(BaseSliderView.ScaleType.FitCenterCrop);

                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle()
                                        .putString("extra",name);

                                sliderLayout.addSlider(textSliderView);


                            }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sliderLayout.setDuration(3000);

        sliderLayout.addOnPageChangeListener(RoomImages.this);


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
}
