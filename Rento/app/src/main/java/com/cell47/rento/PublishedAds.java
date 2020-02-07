package com.cell47.rento;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.google.android.gms.ads.InterstitialAd;

import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.internal.in;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PublishedAds extends AppCompatActivity implements RewardedVideoAdListener {

    private RewardedVideoAd mRewardedVideoAd;
    private DatabaseReference mDatabase,mlikedadsref,mref;
    private RecyclerView mBlogList,rvlikedads;
    FirebaseRecyclerAdapter<BlogMyAds, PublishedAds.BlogViewHolder> firebaseRecyclerAdapter,firebaseRecyclerAdapter2;
    ProgressBar progressBar2;
    SharedPreferences sharedPreferences;
    private StorageReference mStorageRef;
    Button bmaps;
    private DatabaseReference mDataBaseRefadmin;
    private String userChoosenTask;
    TextView tvpnum,tvcity;
    private Button b1,b2,b3, add;
    String refkey;
    private EditText name,email,fbusername;
    private CircularImageView profilepic;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public  String photofilename="photo.jpg",imageurl;
    private Uri imgUri;
    public final String APP_TAG = "MyCustomApp";
    ProgressDialog dialog;
    public static final String FB_STORAGE_PATH = "image/";
    TextView tvusername, coins;
    SharedPreferences.Editor editor;
    ImageView ivad;
    Integer in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_published_ads);
        StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newbuilder.build());

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        coins = findViewById(R.id.text1);
        add = findViewById(R.id.add);
        mBlogList = findViewById(R.id.rvmyads);
        rvlikedads=findViewById(R.id.rvlikedads);
        ivad=findViewById(R.id.ivrentoad);
        progressBar2=findViewById(R.id.progressBar2);

        name=findViewById(R.id.profname);
        email=findViewById(R.id.profemail);
        fbusername=findViewById(R.id.proffb);
        profilepic=findViewById(R.id.ivuserpic);
        b1=findViewById(R.id.namedone);
        b2=findViewById(R.id.emaildone);
        b3=findViewById(R.id.fbdone);
        tvusername=findViewById(R.id.tvusername);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", null)).child("coins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String str = dataSnapshot.getValue(String.class)+"/"+"1000";
                coins.setText(str);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dialog=new ProgressDialog(PublishedAds.this);
        mDataBaseRefadmin=FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null));
        setName();
        setImage();
        mBlogList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        rvlikedads.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        tvcity=findViewById(R.id.tvcurrentcity);
        tvpnum=findViewById(R.id.tvpnum);

        actionBar.setTitle("My Profile");
        bmaps = findViewById(R.id.bmaps);
        mref=FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null));
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mref.child("Name").setValue(name.getText().toString());
                Toast.makeText(PublishedAds.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mref.child("Email").setValue(email.getText().toString());
                Toast.makeText(PublishedAds.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mref.child("fb").setValue(fbusername.getText().toString());
                Toast.makeText(PublishedAds.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        });
        bmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }
            }
        });
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone","1234567890"))
                .child("myads");
        mlikedadsref= FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone","1234567890"))
                .child("likedads");
        loadData();
        loadlikedads();
        tvcity.setText(sharedPreferences.getString("city",null));
        tvpnum.setText(sharedPreferences.getString("phone",null));


        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        loadRewardedVideoAd();

        loadAd();

    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-6950521191905333/2537626051",
                new AdRequest.Builder().build());
    }

    private void loadAd() {
        FirebaseDatabase.getInstance().getReference().child("controls").child("adurl")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            ivad.setVisibility(View.VISIBLE);
                            Picasso.with(getApplicationContext()).load(dataSnapshot.getValue(String.class)).fit().into(ivad);
                        }
                        else {
                            ivad.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setName() {
        tvusername.setText(sharedPreferences.getString("name","User Name"));
        email.setText(sharedPreferences.getString("email",""));
        fbusername.setText(sharedPreferences.getString("fbusername",""));
        editor=sharedPreferences.edit();
        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    tvusername.setText(dataSnapshot.getValue(String.class));
                    name.setText(dataSnapshot.getValue(String.class));
                    editor.putString("name",dataSnapshot.getValue(String.class));
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("Email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    email.setText(dataSnapshot.getValue(String.class));
                    editor.putString("email",dataSnapshot.getValue(String.class));
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("fb").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    fbusername.setText(dataSnapshot.getValue(String.class));
                    editor.putString("fbusername",dataSnapshot.getValue(String.class));
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(PublishedAds.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(PublishedAds.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void setImage()
    {
        mDataBaseRefadmin.child("imgurl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    imageurl=dataSnapshot.getValue(String.class);
                    if (imageurl!=null){
                        try {
                            Picasso.with(PublishedAds.this).load(imageurl).placeholder(R.drawable.userpic).into(profilepic); //Resolves null pointer exception
                        }
                        catch (NullPointerException e){

                            profilepic.setImageResource(R.drawable.userpic);
                        }
                    }

                }
                else {
                    profilepic.setImageResource(R.drawable.userpic);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,getPhotoFileUri(photofilename));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private Uri getPhotoFileUri(String photofilename) {
        if (isExternalStorageAvailable()) {
            File mediaStoreDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            if (!mediaStoreDir.exists() && !mediaStoreDir.mkdirs()) {

            }
            return Uri.fromFile(new File(mediaStoreDir.getPath() + File.separator + photofilename));

        }
        return null;
    }
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {

        if (data != null && data.getData() != null)
            imgUri = data.getData();

        imgUri=getPhotoFileUri(photofilename);

        try {
            if(imgUri!=null) {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);

                profilepic.setImageBitmap(bm);
                uploadImage(imgUri);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        if (data != null && data.getData() != null)
            imgUri = data.getData();
        else if(data!=null ) {
            Bitmap b = (Bitmap) data.getExtras().get(("data"));
        }

        try {
            if(imgUri!=null) {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                profilepic.setImageBitmap(bm);
                uploadImage(imgUri);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void uploadImage(Uri uri) {
        dialog.setTitle("Uploading Photos..");
        dialog.show();
        int r = 0;
        final StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(uri));


        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getApplicationContext(), "image uploaded", Toast.LENGTH_LONG).show();
                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String Url = Objects.requireNonNull(task.getResult()).toString();
                        mDataBaseRefadmin.child("imgurl").setValue(Url);
                            dialog.dismiss();
                            Toast.makeText(PublishedAds.this, "Profile Pic Updated", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage("uploaded " + (int) progress + "%");
                    }
                });
    }



    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }


    private void loadlikedads() {

        firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<BlogMyAds, PublishedAds.BlogViewHolder>
                (BlogMyAds.class, R.layout.liked_ads, PublishedAds.BlogViewHolder.class, mlikedadsref) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final BlogMyAds model, final int position) {

                if (progressBar2.getVisibility()==View.VISIBLE){
                    progressBar2.setVisibility(View.GONE);
                }

                FirebaseDatabase.getInstance().getReference().child("ads").child(model.getcity()).child(model.getRandom()).child("thumbnail")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    viewHolder.setThumbnail(dataSnapshot.getValue().toString(),getApplicationContext());
                                }
                                else {
                                    mref=getRef(position);
                                    String key=mref.getKey();
                                    FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone","1234567890"))
                                            .child("likedads").child(key).setValue(null);
                                    viewHolder.mView.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mref=getRef(position);
                        String key=mref.getKey();
                        if (key!=null){
                            startActivity(new Intent(getApplicationContext(),AdUploaded.class).putExtra("pushid",mref.getKey()));
                        }
                        else {
                            Toast.makeText(PublishedAds.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }


        };
        rvlikedads.setAdapter(firebaseRecyclerAdapter2);
    }

    private void loadData() {

            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BlogMyAds, PublishedAds.BlogViewHolder>
                    (BlogMyAds.class, R.layout.blog_my_ads, PublishedAds.BlogViewHolder.class, mDatabase) {

                @Override
                protected void populateViewHolder(final BlogViewHolder viewHolder, final BlogMyAds model, final int position) {


                    if (progressBar2.getVisibility()==View.VISIBLE){
                        progressBar2.setVisibility(View.GONE);
                    }


                            FirebaseDatabase.getInstance().getReference().child("ads").child(model.getcity()).child(model.getRandom()).child("price")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                viewHolder.mView.setVisibility(View.VISIBLE);
                                                viewHolder.setPrice(dataSnapshot.getValue().toString());
                                            }
                                            else {
                                                mref=getRef(position);
                                                String key=mref.getKey();
                                                FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone","1234567890"))
                                                        .child("myads").child(key).setValue(null);
                                                viewHolder.mView.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                            FirebaseDatabase.getInstance().getReference().child("ads").child(model.getcity()).child(model.getRandom()).child("thumbnail")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                viewHolder.setThumbnail(dataSnapshot.getValue().toString(),getApplicationContext());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                            FirebaseDatabase.getInstance().getReference().child("ads").child(model.getcity()).child(model.getRandom()).child("tagline")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                viewHolder.setTagLine(dataSnapshot.getValue().toString());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                            viewHolder.bsold.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    android.support.v7.app.AlertDialog.Builder alertDialogBuilder =
                                            new android.support.v7.app.AlertDialog.Builder(PublishedAds.this);
                                    final android.support.v7.app.AlertDialog alertDialog;
                                    alertDialog = alertDialogBuilder.create();
                                    alertDialogBuilder.setTitle("Are you sure to remove the Ad ?");
                                    alertDialogBuilder.setIcon(R.drawable.rento);
                                    alertDialogBuilder.setMessage("This will delete your ad from the list");
                                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            alertDialog.dismiss();


                                        }
                                    });
                                    alertDialogBuilder.setPositiveButton("Yes,Delete Ad", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseDatabase.getInstance().getReference().child("ads").child(model.getcity())
                                                    .child(model.getRandom()).setValue(null)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(PublishedAds.this, "Your Ad was removed", Toast.LENGTH_SHORT).show();
                                                            viewHolder.mView.setVisibility(View.GONE);
                                                        }
                                                    });
                                            alertDialog.dismiss();
                                        }
                                    });

                                    //alertDialog.show();
                                    alertDialogBuilder.show();





                                }
                            });

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mref=getRef(position);
                            String key=mref.getKey();
                            mref.child("random").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    refkey=dataSnapshot.getValue(String.class);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                                    if (refkey != null) {
                                        Intent i = new Intent(PublishedAds.this, AdUploaded.class);
                                        i.putExtra("pushid", refkey);

                                        startActivity(i);

                                    }


                        }
                    });

                        }


            };
            mBlogList.setAdapter(firebaseRecyclerAdapter);
        }

    @Override
    public void onRewarded(final RewardItem reward) {

        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", null)).child("coins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                in = Integer.valueOf(dataSnapshot.getValue(String.class));
                in = in+reward.getAmount();
                FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", null)).child("coins").setValue(String.valueOf(in));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(this, "some error occured", Toast.LENGTH_SHORT).show();
            }
        });
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        //Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                //Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        //Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        //Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "Your Ad is loaded, Now you can watch tha... ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        //Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        //Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
        //Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Button bsold;


        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            bsold=mView.findViewById(R.id.bsold);

            //ratingBar=mView.findViewById(R.id.ratingBar);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //mClickListener.onItemClick(v, getAdapterPosition());

                }
            });

        }

        public void setThumbnail(String thumbnail,Context context){

            final ImageView i=mView.findViewById(R.id.ivmyads);
            if (thumbnail!=null)
                Picasso.with(context).load(thumbnail).fit().into(i);
            else
                i.setImageResource(R.drawable.rento);
        }

        public void setPrice(String price){
            TextView post_aval=mView.findViewById(R.id.tvpricemyads);
            post_aval.setText("Rs "+price+"/mo");
        }

        public void setTagLine(String tagLine){
            TextView post_aval=mView.findViewById(R.id.tvtaglinemyads);
            post_aval.setText(tagLine);
        }

    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    private void cancelalert() {

    }

}
