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
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class UploadPicture extends AppCompatActivity  {

    private static int currentPage = 0;
    private static ViewPager viewPager;
    private Button buttonUpload;
    private EditText et_title;
    private EditText et_price,et_area;
    private EditText et_desc,landmark,housenum;
    public static final int REQUEST_CODE_UPLOAD = 1234,REQUEST_CODE_CAPTURE=1111;
    private StorageReference mStorageRef;
    private DatabaseReference mDataBaseRefadmin,mref;
    private Button b1,b2,b3;
    private Uri imgUri;
    public static final String FB_STORAGE_PATH = "image/";
    private ArrayList<Uri> imageAdapter;
    private Uri UriArray[];
    String s1, Cat, username;
    Switch r;
    ProgressDialog dialog;
    private int i = 0, count = 0;
    String random_string;
    private ImageButton uploadImage,captureImage;
    TextView next;
    Double latitude, longitude;
    SharedPreferences sharedPreferences;
    RadioGroup rg;
    RadioButton room,flat,house;
    Spinner windows,almirah,spref;
    String nw,na,selectedId;
    String prefnum;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect;
    private ImageView ivImage;
    private String userChoosenTask;
    public  String photofilename="photo.jpg";
    public final String APP_TAG = "MyCustomApp";

    FirebaseRecyclerAdapter<BlogArea,AreaHolder> firebaseRecyclerAdapter3;
    private RecyclerView rvareas;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);
        StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newbuilder.build());
        uploadImage = findViewById(R.id.badd);
        imageAdapter = new ArrayList<Uri>();
        UriArray = new Uri[1];

        mStorageRef = FirebaseStorage.getInstance().getReference();
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        username = sharedPreferences.getString("phone", null);
        next = findViewById(R.id.next);

        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        et_title = findViewById(R.id.title12);
        et_price = findViewById(R.id.Price12);
        et_desc = findViewById(R.id.desc);
        et_area=findViewById(R.id.tvarea);
        landmark=findViewById(R.id.landmark);
        housenum=findViewById(R.id.housenumber);
        buttonUpload = findViewById(R.id.buttonUplaod);
        r = findViewById(R.id.recommendedswitch);
        rg=findViewById(R.id.rg);
        room=findViewById(R.id.rbroom);
        flat=findViewById(R.id.rbflat);
        house=findViewById(R.id.rbhouse);
        windows=findViewById(R.id.swindows);
        almirah=findViewById(R.id.salmirah);
        spref=findViewById(R.id.spreference);

        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                i = 1;
                startActivityForResult(Intent.createChooser(intent, "select image"), REQUEST_CODE_UPLOAD);*/

                selectImage();
            }
        });


        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postAd();


            }
        });


        if (sharedPreferences.getString("city", null)!=null){
            mDataBaseRefadmin = FirebaseDatabase.getInstance().getReference().child("ads").child(sharedPreferences.getString("city", null).toUpperCase());
            random_string = mDataBaseRefadmin.push().getKey();

            mDataBaseRefadmin = FirebaseDatabase.getInstance().getReference().child("ads").child(sharedPreferences.getString("city", null)
            .toUpperCase()).child(random_string);

        }
        else {
            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            finish();
        }


        ArrayAdapter<CharSequence> adaptera = ArrayAdapter.createFromResource(this,
                R.array.nalmirah, android.R.layout.simple_spinner_item);



        adaptera.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        almirah.setAdapter(adaptera);
        almirah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] values = getResources().getStringArray(R.array.nalmirah);
                na=(values[i]);
               // Toast.makeText(UploadPicture.this, na+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<CharSequence> adapterw = ArrayAdapter.createFromResource(this,
                R.array.nwindow, android.R.layout.simple_spinner_item);
        adapterw.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        windows.setAdapter(adapterw);
        windows.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] values = getResources().getStringArray(R.array.nwindow);
                nw=(values[i]);
               // Toast.makeText(UploadPicture.this, nw+"", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<CharSequence> adapterpref = ArrayAdapter.createFromResource(this,
                R.array.roomtype, android.R.layout.simple_spinner_item);


        spref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] values = getResources().getStringArray(R.array.roomtype);
                prefnum= String.valueOf(values[i]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spref.setAdapter(adapterpref);



        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rbroom:
                        selectedId="1";
                        //Toast.makeText(UploadPicture.this, ""+selectedId, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rbflat:
                        selectedId="2";
                       // Toast.makeText(UploadPicture.this, ""+selectedId, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rbhouse:
                        selectedId="3";
                        //Toast.makeText(UploadPicture.this, ""+selectedId, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        rvareas=findViewById(R.id.rvareasup);
        LinearLayoutManager layoutManager =
                new GridLayoutManager(this, 4, GridLayoutManager.HORIZONTAL, false);
        rvareas.setLayoutManager(layoutManager);
        loadareas();
    }


    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadPicture.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(UploadPicture.this);

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


    private void postAd() {
        //startPayment();

        if ((et_desc.getText().equals(""))||(et_price.getText().length()==0)||et_title.getText().length()==0||imgUri==null||
                housenum.getText().length()==0||et_area.getText().length()==0){
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
        }
        else {
            if (sharedPreferences.getString("postalcode", null)!=null){
                Long tsLong = System.currentTimeMillis() / 1000;
                /*FirebaseDatabase.getInstance().getReference().child("cities").child(sharedPreferences.getString("postalcode",null))
                        .child("city")
                        .setValue(sharedPreferences.getString("city",null));

                FirebaseDatabase.getInstance().getReference().child("cities").child(sharedPreferences.getString("postalcode",null))
                        .child("postalcode")
                        .setValue(sharedPreferences.getString("postalcode",null));*/
                String key=FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone","1234567890"))
                        .child("myads").push().getKey();

                FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone","1234567890"))
                        .child("myads").child(key).child("random").setValue(random_string);

                FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone","1234567890"))
                        .child("myads").child(key).child("city").setValue(sharedPreferences.getString("city","").toUpperCase());

                if (sharedPreferences.getString("area",null)!=null&&sharedPreferences.getString("city",null)!=null)
                {
                    FirebaseDatabase.getInstance().getReference().child("areas")
                            .child(Objects.requireNonNull(sharedPreferences.getString("city", null)).toUpperCase())
                            .child(et_area.getText().toString())
                            .child("area").setValue(et_area.getText().toString());
                    mDataBaseRefadmin.child("area").setValue(et_area.getText().toString().toUpperCase());

                }
                else if (sharedPreferences.getString("locality",null)!=null&&sharedPreferences.getString("city",null)!=null){
                    FirebaseDatabase.getInstance().getReference().child("areas")
                            .child(Objects.requireNonNull(sharedPreferences.getString("city", null)).toUpperCase())
                            .child(et_area.getText().toString())
                            .child("area").setValue(et_area.getText().toString());
                    mDataBaseRefadmin.child("area").setValue(et_area.getText().toString().toUpperCase());

                }

                mDataBaseRefadmin.child("status").setValue("0");
                mDataBaseRefadmin.child("housenum").setValue(housenum.getText().toString());
                mDataBaseRefadmin.child("landmark").setValue(landmark.getText().toString());
                mDataBaseRefadmin.child("timestamp").setValue(tsLong.toString());
                mDataBaseRefadmin.child("price").setValue(et_price.getText().toString());
                mDataBaseRefadmin.child("tagline").setValue(et_title.getText().toString());
                mDataBaseRefadmin.child("address").setValue(et_desc.getText().toString());
                mDataBaseRefadmin.child("contact").setValue(sharedPreferences.getString("phone", null));
                mDataBaseRefadmin.child("name").setValue(sharedPreferences.getString("name", null));
                mDataBaseRefadmin.child("latitude").setValue(latitude);
                mDataBaseRefadmin.child("type").setValue(parseId(selectedId));
                mDataBaseRefadmin.child("window").setValue(nw+"");
                mDataBaseRefadmin.child("almirah").setValue(na+"");
                mDataBaseRefadmin.child("pref").setValue(prefnum+"");
                mDataBaseRefadmin.child("lonitude").setValue(longitude).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for (i = 0; i < imageAdapter.size(); i++) {
                            uploadImage(imageAdapter.get(i), i);
                        }

                    }
                });
            }
            else {
                Toast.makeText(this, "Couldn't get your postal code,Try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public String parseId(String id)
    {
        String type="";
        if(id.equals("1"))
            type="Room";
        else if(id.equals("2"))
            type="Flat";
        else if(id.equals("3"))
            type="House";
        return type;
    }



    private void uploadImage(Uri uri, final int img) {
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
                        mDataBaseRefadmin.child("pics").push().setValue(Url);
                        if (img==0){
                            mDataBaseRefadmin.child("thumbnail").setValue(Url);
                        }
                        if (img+1 == imageAdapter.size()) {
                            dialog.dismiss();
                            Toast.makeText(UploadPicture.this, "Your Ad is Uploaded", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),AdUploaded.class)
                            .putExtra("pushid",random_string).putExtra("flag",0));
                            finish();
                        }

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {

        /*Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        assert thumbnail != null;
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;

        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data != null && data.getData() != null)
            imgUri = data.getData();
        else if(data!=null ) {
            Bitmap b = (Bitmap) data.getExtras().get(("data"));
        }*/
        imgUri=getPhotoFileUri(photofilename);

        try {
            if(imgUri!=null) {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                UriArray[0]=imgUri;
                Log.d("imageuri",UriArray[0].toString());
                init();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ivImage.setImageBitmap(thumbnail);
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
                UriArray[0]=imgUri;
                Log.d("imageuri",UriArray[0].toString());
                init();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }



    @Override
    public void onBackPressed() {
        if (dialog.isShowing()){
            Toast.makeText(this, "Wait for Image to upload", Toast.LENGTH_SHORT).show();
        }
        else
            finish();
    }


    private void init() {
        //TODO: Arraylist of uri welcome array,array of Uri

        if (imageAdapter.size()>0){
            next.setVisibility(View.VISIBLE);
            next.setText("Swipe to see all pictures");
        }

        imageAdapter.addAll(Arrays.asList(UriArray));
        viewPager = (ViewPager) findViewById(R.id.view_pager2);
        viewPager.setAdapter(new UploadAdapter(UploadPicture.this, imageAdapter));//TODO: Arraylist chahiye uri wali yha
        currentPage = imageAdapter.size();
        viewPager.setCurrentItem(currentPage, true);
        // Auto start of viewpager
    }

    private void loadareas() {
        //llcv.setVisibility(View.VISIBLE);
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
                        //loadareafiltered(model.getArea().toUpperCase());
                        et_area.setText(model.getArea());

                    }
                });

            }

        };

        //ibnofilter.setVisibility(View.VISIBLE);
        rvareas.setAdapter(firebaseRecyclerAdapter3);
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
}