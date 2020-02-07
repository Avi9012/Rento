package com.cell47.rento;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AllListedCities extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private RecyclerView mBlogList;
    FirebaseRecyclerAdapter<ListCities, BlogViewHolder> firebaseRecyclerAdapter;
    SharedPreferences sharedPreferences;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_listed_cities);

        actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Rento Enabled Cities");
        actionBar.hide();
        mBlogList = findViewById(R.id.rvallcities);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        loadData();
    }


    private void loadData() {

        mDatabase= FirebaseDatabase.getInstance().getReference().child("controls").child("serviceablecities");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ListCities, BlogViewHolder>
                (ListCities.class, R.layout.layout_room_images, BlogViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, final ListCities model, int position) {
                //Toast.makeText(AllListedCities.this, model.getUrl(), Toast.LENGTH_SHORT).show();
                viewHolder.setThumbnail(model.getUrl(),getApplicationContext());
                viewHolder.setName(model.getName());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("city",model.getName().toUpperCase());
                        editor.apply();
                        startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                    }
                });
            }



        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;


        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setThumbnail(String thumbnail,Context context){

            final ImageView i=mView.findViewById(R.id.ivcityimage);
            if (thumbnail!=null)
                Picasso.with(context).load(thumbnail).fit().into(i);
            else
                i.setImageResource(R.drawable.rento);
        }

        public void setName(String name){
            TextView cityname=mView.findViewById(R.id.tvcityname);
            cityname.setText(name);
        }

    }

}


