package com.cell47.rento;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    RelativeLayout uplayout;
    Animation uptodown,lefttoright;
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    ProgressBar progressBar;
    TextView onoffline;
    private Marker currentlocmarker;
    public static final int REQUEST_LOCATION_CODE=99;
    List<Address> addressList=null;

    Double currentLatitude,currentLongitude;
    TextView tv_currentLoc;
    String currentLocation;

    LatLng latLng,m;
    Button proceedbutton,buttoncities;
    MarkerOptions markerOptions,mo;

    int count=0,check=0;
    AlertDialog alert;
    int onoroffloccheck=0,i=0,b=1,d=1,intentcheck=1;
    FirebaseRecyclerAdapter<BlogCities, MapsActivity.BlogViewHolder> firebaseRecyclerAdapter;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    LinearLayout llsearch;
    MaterialSearchBar materialSearchBar;
    List<String> suggestList=new ArrayList<>();
    FirebaseRecyclerAdapter<BlogCities,BlogViewHolder> searchadapter;
    private DatabaseReference dbref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            checkLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        intentcheck=getIntent().getIntExtra("check",1);

        tv_currentLoc=findViewById(R.id.tv_currentLocation);

        proceedbutton=findViewById(R.id.button_proceed);
        buttoncities=findViewById(R.id.button_cities);
        onoffline=findViewById(R.id.onoffline);
        progressBar = findViewById(R.id.progressbar);
        uplayout=findViewById(R.id.uplayout);
        llsearch=findViewById(R.id.llsearch);


        mBlogList = findViewById(R.id.rvcities);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase= FirebaseDatabase.getInstance().getReference().child("cities");


        materialSearchBar=findViewById(R.id.search);
        loadSuggestions();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setHint("Search for cities here");
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest=new ArrayList<>();
                for (String search:suggestList){
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())){
                        suggest.add(search);

                        mBlogList.setAdapter(searchadapter);

                    }
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled){
                    closeKeyboard();
                }

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
                closeKeyboard();
                count=0;

            }

            @Override
            public void onButtonClicked(int buttonCode) {

                mBlogList.setAdapter(firebaseRecyclerAdapter);
                loadSuggestions();
            }
        });




    }




    private void startSearch(CharSequence text) {
        materialSearchBar.setFocusable(false);
        count=0;
        searchadapter=new FirebaseRecyclerAdapter<BlogCities, BlogViewHolder>(
                BlogCities.class,
                R.layout.blog_row_cities,
                BlogViewHolder.class,
                dbref.orderByChild("city").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, final BlogCities model, int position) {

                viewHolder.setCity(model.getCity());
                viewHolder.setPostalCode(model.getPostalcode());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("postalcode",model.getPostalcode());
                        editor.putString("city",model.getCity());
                        editor.apply();
                        startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                        finish();
                    }
                });
            }

        };

        mBlogList.setAdapter(searchadapter);
    }







    private void closeKeyboard() {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), 0);

    }
    private void loadSuggestions() {
        dbref= FirebaseDatabase.getInstance().getReference().child("cities");
        dbref.orderByChild("city").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    BlogCities blogMenu=postSnapshot.getValue(BlogCities.class);
                    assert blogMenu != null;
                    suggestList.add(blogMenu.getCity());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loaddata() {
        mBlogList.setVisibility(View.VISIBLE);
        llsearch.setVisibility(View.VISIBLE);
        //tv_currentLoc.setVisibility(View.GONE);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BlogCities, MapsActivity.BlogViewHolder>
                (BlogCities.class, R.layout.blog_row_cities, MapsActivity.BlogViewHolder.class, mDatabase) {




            @Override
            protected void populateViewHolder(final MapsActivity.BlogViewHolder viewHolder, final BlogCities model, final int position) {
                {

                    viewHolder.setCity(model.getCity());
                    viewHolder.setPostalCode(model.getPostalcode());
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            if (model.getPostalcode()!=null){
                                editor.putString("postalcode",model.getPostalcode());
                                editor.putString("city",model.getCity());
                                editor.apply();
                                startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                                finish();
                            }
                            else
                            {
                                Toast.makeText(MapsActivity.this, "Error selecting city", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }



            }


        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (alert!=null)
            alert.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkCheck();
        displayLocationSettingsRequest(this);

    }

    private void NetworkCheck() {

        if (isNetworkAvailable(this)){
            onoffline.setBackgroundResource(R.color.online);

        }
        else {
            check=0;
            AlertDialog.Builder builder=new AlertDialog.Builder(MapsActivity.this);
            builder.setTitle("NO INTERNET");
            builder.setMessage("this app requires internet to work!");
            builder.setCancelable(true);
            onoffline.setBackgroundResource(R.color.offline);

            alert=builder.create();
            alert.show();
        }
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo()!=null
                &&connectivityManager.getActiveNetworkInfo().isConnected();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    {
                        if (client==null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                }
                else
                {
                    Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        proceedbutton.setVisibility(View.VISIBLE);
        proceedbutton.setAnimation(lefttoright);
        uplayout.setAnimation(uptodown);

    }

    protected synchronized void buildGoogleApiClient(){
        client=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest=new LocationRequest();

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    @Override
    public void onLocationChanged(final Location location) {
        mMap.clear();
        count++;

        if (currentlocmarker != null) {
            currentlocmarker.remove();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                markerOptions=new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Search Location");
                mMap.addMarker(markerOptions);


                currentLatitude = latLng.latitude;
                currentLongitude = latLng.longitude;
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 1200, null);
                    markerOptions=new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Your Location");
                    mMap.addMarker(markerOptions);
                    addressList = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                    currentLocation = addressList.get(0).getAddressLine(0);

                    //Toast.makeText(MapsActivity.this, addressList.get(0).getLocality()+"", Toast.LENGTH_SHORT).show();
                    tv_currentLoc.setText(currentLocation);
                    /*tv_currentLoc.setText(tv_currentLoc.getText()+"\n"+"Locality :"+addressList.get(0).getLocality()+"\nSubLocality "+addressList.get(0).getSubLocality()
                            +"\nAdmin Area "+addressList.get(0).getAdminArea()+"\nSubAdmin "+addressList.get(0).getSubAdminArea()+
                            "\nmaxaddlineindex"+addressList.get(0).getMaxAddressLineIndex()+"\nfeature name"+addressList.get(0).getFeatureName()+
                            "\nextras "+addressList.get(0).getExtras()+"\nadd "+addressList.get(0).getPremises()+"\n"+addressList.get(0).getLocale()+"\n"
                            +addressList.get(0).describeContents()+"\n"+addressList.get(0).getThoroughfare());
*/

                    SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    if (addressList.get(0).getPostalCode()!=null){
                        editor.putString("postalcode",addressList.get(0).getPostalCode());
                        editor.putString("city",addressList.get(0).getSubAdminArea());
                        editor.putString("area",addressList.get(0).getSubLocality());
                        if (!addressList.get(0).getSubAdminArea().equals(addressList.get(0).getLocality())){
                            editor.putString("locality",addressList.get(0).getLocality());
                        }
                        else {
                            editor.putString("locality",null);
                        }
                        editor.apply();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(this);
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 1200, null);
            markerOptions=new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Your Location");
            mMap.addMarker(markerOptions);
            addressList = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
            currentLocation = addressList.get(0).getAddressLine(0);
            //Toast.makeText(this, addressList.get(0).getLocality()+"", Toast.LENGTH_SHORT).show();
            tv_currentLoc.setText(currentLocation);

            /*tv_currentLoc.setText(tv_currentLoc.getText()+"\n"+"Locality :"+addressList.get(0).getLocality()+"\nSubLocality "+addressList.get(0).getSubLocality()
                    +"\nAdmin Area "+addressList.get(0).getAdminArea()+"\nSubAdmin "+addressList.get(0).getSubAdminArea()+
                    "\nmaxaddlineindex"+addressList.get(0).getMaxAddressLineIndex()+"\nfeature name"+addressList.get(0).getFeatureName()+
                    "\nextras "+addressList.get(0).getExtras()+"\nadd "+addressList.get(0).getPremises()+"\n"+addressList.get(0).getLocale()+"\n"
            +addressList.get(0).describeContents()+"\n"+addressList.get(0).getThoroughfare());*/



            SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor=sharedPreferences.edit();

                editor.putString("postalcode",addressList.get(0).getPostalCode());
                editor.putString("city",addressList.get(0).getSubAdminArea());
                editor.putString("area",addressList.get(0).getSubLocality());
                if (!addressList.get(0).getSubAdminArea().equals(addressList.get(0).getLocality())){
                    editor.putString("locality",addressList.get(0).getLocality());
                }
                else {
                    editor.putString("locality",null);
                }
                editor.apply();


        } catch (IOException e) {
            e.printStackTrace();
        }


        if (isNetworkAvailable(this)) {
            onoffline.setBackgroundResource(R.color.online);
        } else {
            onoffline.setBackgroundResource(R.color.offline);
            Toast.makeText(this, "offline", Toast.LENGTH_SHORT).show();
        }


        if (intentcheck==0){
            buttoncities.setVisibility(View.VISIBLE);
            buttoncities.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loaddata();
                    Toast.makeText(MapsActivity.this, "Click on the list to select city", Toast.LENGTH_SHORT).show();
                }
            });

            proceedbutton.setText("Use Marker location");
            proceedbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                    finish();
                }
            });
        }
        else {
            buttoncities.setVisibility(View.GONE);
            //Toast.makeText(this, "Click on map to change location", Toast.LENGTH_SHORT).show();
            proceedbutton.setText("Continue to post AD");
            proceedbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(),UploadPicture.class)
                    .putExtra("latitude",currentLatitude)
                    .putExtra("longitude",currentLongitude)
                    );
                    finish();
                }
            });
        }




    }


    public boolean checkLocationPermission(){

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
    }



    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        /*locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(50000);
        locationRequest.setFastestInterval(50000 / 2);*/

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        onoroffloccheck=1;
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        onoroffloccheck=0;

                        try {
                            status.startResolutionForResult(MapsActivity.this, REQUEST_LOCATION_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            return;
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;





        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


            //ratingBar=mView.findViewById(R.id.ratingBar);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //mClickListener.onItemClick(v, getAdapterPosition());

                }
            });

        }

        private HomeScreen.BlogViewHolder.ClickListener mClickListener;





        public void setCity(String city){
            TextView post_aval=mView.findViewById(R.id.tvcity);
            post_aval.setText(city);

        }
        public void setPostalCode(String postalCode){
            TextView post_aval=mView.findViewById(R.id.tvpostalcode);
            post_aval.setText(postalCode);

        }

    }



}