package com.cell47.rento;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Comment extends AppCompatActivity {
    private String random_string, city;
    private DatabaseReference mref;
    private TextView tv;
    private ImageView send;
    private String curr_message, user, name;
    private RecyclerView comments;
    private List<Commenthelp> commenthelpList;
    private SharedPreferences sharedPreferences;
    private CommentAdapter CommentAdap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getSupportActionBar().setTitle("Comments");
        random_string = getIntent().getStringExtra("pushid");
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        city = sharedPreferences.getString("city", null);
        tv = findViewById(R.id.messageArea);
        commenthelpList = new ArrayList<>();
        comments = findViewById(R.id.rvcomments);
        comments.setHasFixedSize(true);
        comments.setLayoutManager(new LinearLayoutManager(this));
        send = (ImageView) findViewById(R.id.sendButton);

        mref = FirebaseDatabase.getInstance().getReference().child("ads").child(city.toUpperCase())
                .child(random_string).child("comments");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv.getText().toString().equals("")) {
                    Toast.makeText(Comment.this, "Write some Text", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", null)).child("Name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            HashMap<String, String> map = new HashMap<>();
                            Date date = new Date();
                            String Date = DateFormat.getDateInstance().format(date);
                            String Time = DateFormat.getTimeInstance().format(date);
                            curr_message = tv.getText().toString();
                            map.put("com", curr_message);
                            map.put("userName", dataSnapshot.getValue(String.class));
                            map.put("date", Date);
                            map.put("time", Time);
                            mref.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Comment.this, "Thank you for your comment...", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                            tv.setText("");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commenthelpList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Commenthelp commenthelp = dataSnapshot1.getValue(Commenthelp.class);
                    assert commenthelp != null;
                    commenthelpList.add(commenthelp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        CommentAdap = new CommentAdapter(Comment.this, commenthelpList);
        comments.setAdapter(CommentAdap);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}