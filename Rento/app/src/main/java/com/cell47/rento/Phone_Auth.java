package com.cell47.rento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import static com.cell47.rento.LoginActivity.auth;


public class Phone_Auth extends AppCompatActivity {
    String phone_number,username,passwd,encrypt;
    EditText Otp;
    private String mVerificationId;
    private Button submit;
    private int check=0;
    ProgressDialog pd;
    DatabaseReference mref;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone__auth);

        phone_number=getIntent().getStringExtra("phone");
        username=getIntent().getStringExtra("username");
        passwd=getIntent().getStringExtra("password");
        Firebase.setAndroidContext(this);
        Otp=findViewById(R.id.otp);
        submit=findViewById(R.id.btn);





        sendVerificationCode(phone_number);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Otp.length() == 4)
                {
                    verifyVerificationCode(Otp.getText().toString());
                    Toast.makeText(Phone_Auth.this, "sent", Toast.LENGTH_SHORT).show();
                    pd.show();


                }
                else
                    Toast.makeText(Phone_Auth.this, "Invalid", Toast.LENGTH_SHORT).show();


            }
        });

        /*Resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mResendToken!=null) {
                    Toast.makeText(Phone_Auth.this,"OTP Sent",Toast.LENGTH_SHORT).show();
                    pd.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phone_number,        // Phone number to verify
                            60,               // Timeout duration
                            TimeUnit.MINUTES,   // Unit of timeout
                            Phone_Auth.this,               // Activity (for callback binding)
                            mCallbacks,         // OnVerificationStateChangedCallbacks
                            mResendToken);      // Force Resending Token from callbacks
                }
                Toast.makeText(Phone_Auth.this,"Please wait!",Toast.LENGTH_SHORT).show();


            }
        });*/
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Phone Number Verification");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
    }
    public void registerUser(String s){

        mref= FirebaseDatabase.getInstance().getReference().child("users").child(phone_number);

        try {
            encrypt= EncryptDecrypt.encrypt(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mref.child("password").setValue(encrypt);
        mref.child("Name").setValue(username);
        mref.child("mobile").setValue(phone_number);
        mref.child("admin").setValue("0");
        mref.child("coins").setValue(0);

    }
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {

                //verifying the code
                verifyVerificationCode(code);
                String message = "Verification Completed!";

                final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Phone_Auth.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //* check=1;*//*
            //storing the verification id that is sent to the user
            mVerificationId = s;
            mResendToken=forceResendingToken;

        }
    };
    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(Phone_Auth.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            check=1;
                            saveData();
                            registerUser(passwd);
                            Toast.makeText(Phone_Auth.this, "Success!", Toast.LENGTH_LONG).show();
                            SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);

                            if (sharedPreferences.getString("postalcode",null)==null){
                                startActivity(new Intent(Phone_Auth.this,AllListedCities.class)
                                .putExtra("check",0));
                                finish();
                            }
                            else {
                                startActivity(new Intent(Phone_Auth.this,HomeScreen.class));
                                finish();
                            }



                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });

                        }
                    }
                });
    }

    private void saveData() {

        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("verified",true);
        editor.apply();

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
