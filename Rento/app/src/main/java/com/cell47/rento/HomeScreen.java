package com.cell47.rento;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class HomeScreen extends AppCompatActivity implements BaseSliderView.ImageLoadListener, ViewPagerEx.OnPageChangeListener {

    private static final String TAG ="MSG" ;
    private static final int AUTOCOMPLETE_REQUEST_CODE =99 ;
    //Button bmaps;
    private static int currentPage = 0;
    private static final Integer[] welcomeImage = {R.drawable.rento, R.drawable.rento, R.drawable.rento};
    private ArrayList<Integer> WelcomeArray = new ArrayList<Integer>();
    private static ViewPager viewPager;
    private DatabaseReference mDatabase, mref,refareafilter;
    private RecyclerView mBlogList,rvareas;
    FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter, firebaseRecyclerAdapter2,firebaseRecyclerAdapter4;
    FirebaseRecyclerAdapter<BlogArea,AreaHolder> firebaseRecyclerAdapter3;
    SliderLayout sliderLayout;
    HashMap<String, String> HashMapForURL;
    SharedPreferences sharedPreferences;
    public static int visit=0;


    Query query;
    ProgressBar pbhs;
    Spinner sprefselect;
    String prefnumber;
    LinearLayout llpref;
    List<String> suggestList = new ArrayList<>();
    int count = 0;
    ImageButton bremo;
    TextView tvsf;
    Button bmaps;
    CardView cvsearch;
    ImageView usecurrentlocation;
    private DatabaseReference mDataBaseRefadmin;
    String random_string;
    List<Address> addressList = null;
    private DatabaseReference dbref;
    ImageButton ibnofilter;
    LinearLayout llcv;
    TextView noroom;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBcQ-tGdwHU0N2Thl56BWdu8PqoTtWjnrw");
        }
        PlacesClient placesClient = Places.createClient(this);
        ActionBar actionBar = getSupportActionBar();
        MobileAds.initialize(this, "ca-app-pub-6950521191905333~9815042977");

        sliderLayout = findViewById(R.id.slider);
        sliderLayout.setVisibility(View.GONE);
        bmaps=findViewById(R.id.bmaps);
        cvsearch=findViewById(R.id.cvsearch);
        ibnofilter=findViewById(R.id.ibnofilter);
        llcv=findViewById(R.id.llcv);
        noroom=findViewById(R.id.tvnoroom);

        bmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });

        cvsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields).setCountry("IN") //NIGERIA
                        .build(HomeScreen.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //AddImagesUrlOnline();

        usecurrentlocation = findViewById(R.id.ivusecurrlocn);

        mBlogList = findViewById(R.id.partners);
        rvareas=findViewById(R.id.rvareas);
        rvareas.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mBlogList.setLayoutManager(new GridLayoutManager(this, 2));

        pbhs = findViewById(R.id.pbhs);

        llpref = findViewById(R.id.llpref);
        sprefselect = findViewById(R.id.sfilter);
        tvsf = findViewById(R.id.tvsf);
        assert actionBar != null;
        if(sharedPreferences.getString("city", null) != null){
            actionBar.setTitle("Rento " + " (" + sharedPreferences.getString("city", null) + " )");
            loadareas();
        }

        else {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class)
                    .putExtra("check", 0));

        }

        checkareaavailability();

       /* bmaps = findViewById(R.id.bmaps);
        bmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });*/
        // init();

        usecurrentlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeScreen.this, MapsActivity.class)
                        .putExtra("check", 0));
                finish();
            }
        });

        ibnofilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadall();
            }
        });
    }

    private void checkareaavailability() {
        FirebaseDatabase.getInstance().getReference().child("areas").child(sharedPreferences.getString("city",null))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot==null){
                            rvareas.setVisibility(View.GONE);
                            llcv.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadareas() {
        llcv.setVisibility(View.VISIBLE);
        Query query1=FirebaseDatabase.getInstance().getReference().child("areas").child(sharedPreferences.getString("city",null).toUpperCase());

        query1.keepSynced(true);
        firebaseRecyclerAdapter3 = new FirebaseRecyclerAdapter<BlogArea, AreaHolder>
                (BlogArea.class, R.layout.blog_row_areas, AreaHolder.class, query1) {


            @Override
            protected void populateViewHolder(AreaHolder viewHolder, final BlogArea model, int position) {
                viewHolder.setArea(model.getArea());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadareafiltered(model.getArea().toUpperCase());
                    }
                });

            }

        };

        ibnofilter.setVisibility(View.VISIBLE);
        rvareas.setAdapter(firebaseRecyclerAdapter3);
    }

    private void showAddConfirmationDialog(final LatLng coordinates, final String name, final String addressOfLocation) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.


    }

    private void loadad() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("proms").child(sharedPreferences.getString("city", null)
                .toUpperCase());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String name = snapshot.getValue().toString();

                        TextSliderView textSliderView = new TextSliderView(HomeScreen.this);

                        textSliderView
                                .image(name)
                                .setScaleType(BaseSliderView.ScaleType.Fit);

                        textSliderView.bundle(new Bundle());

                        textSliderView.getBundle()
                                .putString("extra", name);

                        sliderLayout.addSlider(textSliderView);
                    }
                } else {
                    sliderLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.DepthPage);

        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        sliderLayout.setCustomAnimation(new DescriptionAnimation());

        sliderLayout.setDuration(3000);

        sliderLayout.addOnPageChangeListener(HomeScreen.this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ads").child(Objects.requireNonNull(sharedPreferences.getString("city", null).toUpperCase()));
        //Toast.makeText(this, mDatabase+"", Toast.LENGTH_SHORT).show();
        mDatabase.keepSynced(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String currentLocation;
                Geocoder geocoder = new Geocoder(HomeScreen.this);
                try {
                    addressList = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    currentLocation=addressList.get(0).getLocality();
                    SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    //editor.putString("postalcode",model.getPostalcode());
                    editor.putString("city",currentLocation.toUpperCase());
                    editor.apply();
                    onStart();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // do query with address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(HomeScreen.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Toast.makeText(this, "Search Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loaddata() {

        Log.d(TAG, "loaddata: "+prefnumber);
        query=FirebaseDatabase.getInstance().getReference().child("ads").child(sharedPreferences.getString("city",null).toUpperCase()).orderByChild("pref").equalTo(prefnumber);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>
                (Blog.class, R.layout.blog_row, BlogViewHolder.class, query) {


            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final Blog model, final int position) {

                {

                    {
                        {
                            //final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mBlogList.getLayoutManager();
                            if (pbhs.getVisibility() == View.VISIBLE) {
                                pbhs.setVisibility(View.GONE);
                            }
                            String check=model.getStatus();
                            if(check.equals("0"))
                            {
                                viewHolder.itemView.setVisibility(View.GONE);
                                return;
                            }

                            viewHolder.setUrl(model.getThumbnail(), HomeScreen.this);
                            viewHolder.setPrice(model.getPrice());
                            viewHolder.setTagline(model.getTagline());
                            viewHolder.setLocality(model.getaddress());
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mref = getRef(position);
                                    startActivity(new Intent(HomeScreen.this, AdUploaded.class).putExtra("pushid", mref.getKey()));
                                    // Toast.makeText(HomeScreen.this, mref.getKey().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            viewHolder.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mref = getRef(position);
                                    String key = mref.getKey();
                                    random_string = mref.getKey();
                                    FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", "1234567890"))
                                            .child("likedads").child(key).child("random").setValue(random_string);

                                    FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", "1234567890"))
                                            .child("likedads").child(key).child("city").setValue(sharedPreferences.getString("city", "").toUpperCase())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    viewHolder.like.setVisibility(View.GONE);
                                                    Toast.makeText(HomeScreen.this, "Added to your liked ads in profile", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });

                            /*viewHolder.approvedisapprove.setVisibility(View.VISIBLE);
                            viewHolder.approvedisapprove.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    viewHolder.approvedisapprove.setText("Approve");
                                    FirebaseDatabase.getInstance().getReference().child("ads").child(sharedPreferences.getString("city",null).toUpperCase())
                                            .getRef().child("status").setValue("1");
                                }
                            });*/
                        }
                    }


                }

            }


        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadareas();

        sprefselect.setVisibility(View.GONE);
        tvsf.setVisibility(View.GONE);
        checkdata();
        if(visit==0) {
            //loadad();
            visit++;
        }
        ArrayAdapter<CharSequence> adapterpreference = ArrayAdapter.createFromResource(this,
                R.array.roomtype, android.R.layout.simple_spinner_item);

        adapterpreference.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sprefselect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] values = getResources().getStringArray(R.array.roomtype);
                prefnumber = values[i];
                if (i != 0) {
                    loaddata();
                } else
                    loadall();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sprefselect.setAdapter(adapterpreference);
        loadall();

        ActionBar actionBar = getSupportActionBar();
        pbhs = findViewById(R.id.pbhs);
        assert actionBar != null;
        if (sharedPreferences.getString("city", null) != null)
            actionBar.setTitle("Rento " + " (" + sharedPreferences.getString("city", null) + " )");
        else {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class)
                    .putExtra("check", 0));
            Toast.makeText(this, "Please select some other Area", Toast.LENGTH_SHORT).show();


            loadareas();

        }

    }

    private void loadareafiltered(String area) {
        refareafilter = FirebaseDatabase.getInstance().getReference().child("ads").child(Objects.requireNonNull(sharedPreferences.getString("city", null).toUpperCase()));
        firebaseRecyclerAdapter4 = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>
                (Blog.class, R.layout.blog_row, BlogViewHolder.class, refareafilter.orderByChild("area").equalTo(area)) {


            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final Blog model, final int position) {
                {
                    //final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mBlogList.getLayoutManager();
                    if (pbhs.getVisibility() == View.VISIBLE) {
                        pbhs.setVisibility(View.GONE);
                    }

                    String check=model.getStatus();
                    if(check.equals("0"))
                    {
                        viewHolder.itemView.setVisibility(View.GONE);
                        return;
                    }
                    
                    viewHolder.setUrl(model.getThumbnail(), HomeScreen.this);
                    viewHolder.setPrice(model.getPrice());
                    viewHolder.setTagline(model.getTagline());
                    viewHolder.setLocality(model.getaddress());
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mref = getRef(position);
                            startActivity(new Intent(HomeScreen.this, AdUploaded.class).putExtra("pushid", mref.getKey()));
                            //Toast.makeText(HomeScreen.this, mref.getKey().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    viewHolder.like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mref = getRef(position);
                            String key = mref.getKey();
                            random_string = mref.getKey();
                            FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", "1234567890"))
                                    .child("likedads").child(key).child("random").setValue(random_string);

                            FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", "1234567890"))
                                    .child("likedads").child(key).child("city").setValue(sharedPreferences.getString("city", "").toUpperCase())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            viewHolder.like.setVisibility(View.GONE);
                                            Toast.makeText(HomeScreen.this, "Added to your liked ads in profile", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });

                }

            }

        };
        mBlogList.setAdapter(firebaseRecyclerAdapter4);
    }


    private void checkdata() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ads").child(Objects.requireNonNull(sharedPreferences.getString("city", null).toUpperCase()));

        //sprefselect.setVisibility(View.GONE);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    pbhs.setVisibility(View.GONE);
                    Toast.makeText(HomeScreen.this, "No rooms available in selected area", Toast.LENGTH_LONG).show();
                    noroom.setVisibility(View.VISIBLE);
                    llcv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadall() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ads").child(Objects.requireNonNull(sharedPreferences.getString("city", null).toUpperCase()));
        firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>
                (Blog.class, R.layout.blog_row, BlogViewHolder.class, mDatabase.orderByChild("timestamp")) {


            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final Blog model, final int position) {

                {
                    //final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mBlogList.getLayoutManager();
                    if (pbhs.getVisibility() == View.VISIBLE) {
                        pbhs.setVisibility(View.GONE);
                    }
                    String check=model.getStatus();
                    if(check != null && check.equals("0"))
                    {
                        viewHolder.itemView.setVisibility(View.GONE);
                        return;
                    }
                    else if(check == null)
                    {
                        //Toast.makeText(HomeScreen.this, check, Toast.LENGTH_SHORT).show();
                    }
                    viewHolder.setUrl(model.getThumbnail(), HomeScreen.this);
                    viewHolder.setPrice(model.getPrice());
                    viewHolder.setTagline(model.getTagline());
                    viewHolder.setLocality(model.getaddress());
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mref = getRef(position);
                            startActivity(new Intent(HomeScreen.this, AdUploaded.class).putExtra("pushid", mref.getKey()));
                            //Toast.makeText(HomeScreen.this, mref.getKey().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    viewHolder.like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mref = getRef(position);
                            String key = mref.getKey();
                            random_string = mref.getKey();
                            FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", "1234567890"))
                                    .child("likedads").child(key).child("random").setValue(random_string);

                            FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", "1234567890"))
                                    .child("likedads").child(key).child("city").setValue(sharedPreferences.getString("city", "").toUpperCase())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            viewHolder.like.setVisibility(View.GONE);
                                            Toast.makeText(HomeScreen.this, "Added to your liked ads in profile", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });



                }



            }


        };
        mBlogList.setAdapter(firebaseRecyclerAdapter2);
    }


    @Override
    public void onStart(BaseSliderView target) {

    }

    @Override
    public void onEnd(boolean result, BaseSliderView target) {

    }

    public void removeslider(View view) {
        sliderLayout.setVisibility(View.GONE);
        //bremo.setVisibility(View.GONE);
        //Toast.makeText(this, "removed", Toast.LENGTH_SHORT).show();

    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView like;



        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            like = mView.findViewById(R.id.like);


            //ratingBar=mView.findViewById(R.id.ratingBar);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //mClickListener.onItemClick(v, getAdapterPosition());

                }
            });

        }

        private ClickListener mClickListener;

        public interface ClickListener {
            public void onItemClick(View view, int position);

        }

        public void setOnClickListener(ClickListener clickListener) {
            mClickListener = clickListener;
        }


        public void setPrice(String price) {
            TextView post_aval = mView.findViewById(R.id.name_tv_items);
            post_aval.setText(price + " /month");

        }

        public void setTagline(String tagline) {
            TextView post_aval = mView.findViewById(R.id.partneraval);
            post_aval.setText(tagline);

        }

        public void setLocality(String address) {
            TextView post_aval = mView.findViewById(R.id.locality);
            post_aval.setText(address);

        }


        public void setUrl(String url, Context context) {
            final ImageView i = mView.findViewById(R.id.img);
            if (url != null)
                Picasso.with(context).load(url).fit().into(i);
            else
                i.setImageResource(R.drawable.rento);


        }


    }


    @Override
    protected void onStop() {

        sliderLayout.stopAutoCycle();

        super.onStop();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        Log.d("Slider Demo", "Page Changed: " + position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflate6 = getMenuInflater();

        inflate6.inflate(R.menu.my_ads, menu);

        MenuInflater inflate7 = getMenuInflater();
        inflate7.inflate(R.menu.contactus, menu);

        MenuInflater inflate8 = getMenuInflater();
        inflate7.inflate(R.menu.filter, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.contactus) {
            startActivity(new Intent(HomeScreen.this, ContactDetails.class));
        }

        if (id == R.id.savedads) {
            startActivity(new Intent(HomeScreen.this, SavedAds.class));
        }

        if (id == R.id.myads) {
            startActivity(new Intent(HomeScreen.this, PublishedAds.class)
                    .putExtra("check", 0));
            //startActivity(new Intent(getApplicationContext(),UserProfile.class));
        }

        if (id == R.id.filter) {
            sprefselect.setVisibility(View.VISIBLE);
            tvsf.setVisibility(View.VISIBLE);
            //sprefselect.performClick();
            sliderLayout.setVisibility(View.GONE);
        }

        return super.onOptionsItemSelected(item);
    }


    public static class AreaHolder extends RecyclerView.ViewHolder {
        View mView;



        public AreaHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setArea(String area) {

            TextView post_aval = mView.findViewById(R.id.tvarea);
            post_aval.setText(area);

        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),AllListedCities.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}