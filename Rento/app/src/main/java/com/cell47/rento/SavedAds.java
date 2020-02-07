package com.cell47.rento;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SavedAds extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private RecyclerView mBlogList;
    FirebaseRecyclerAdapter<ListSavedAds,BlogViewHolder> firebaseRecyclerAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_ads);

        mBlogList = findViewById(R.id.rvsavedads);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("savedads");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ListSavedAds, BlogViewHolder>
                (ListSavedAds.class, R.layout.layout_saved_ads, BlogViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, final ListSavedAds model, int position) {
                viewHolder.setAdd(model.getAddress());
                viewHolder.setName(model.getName());
                viewHolder.contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getContact()));
                        startActivity(intent);
                    }
                });

                viewHolder.viewmaps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String latitude = String.valueOf(model.getLatitude());
                        String longitude = String.valueOf(model.getLongitude());
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        try{
                            startActivity(mapIntent);

                        }catch (NullPointerException e){
                        }
                    }
                });

            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Button contact,viewmaps;



        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            contact=mView.findViewById(R.id.bcallsaved);
            viewmaps=mView.findViewById(R.id.bviewmaps);

        }


        public void setName(String name){
            TextView post_aval=mView.findViewById(R.id.tvnamesaved);
            post_aval.setText(name);
        }
        public void setAdd(String add){
            TextView post_aval=mView.findViewById(R.id.tvaddresssaved);
            post_aval.setText(add);
        }





    }
}
