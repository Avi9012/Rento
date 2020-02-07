package com.cell47.rento;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_username, et_password,et_phone;
    private Button registerButton;
    private String user, pass,phone;
    private TextView login,tnc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        et_phone=findViewById(R.id.phone);
        et_username =  findViewById(R.id.username);
        et_password =  findViewById(R.id.password);
        tnc=findViewById(R.id.tnc);
        tnc.setText("terms & conditions");

        registerButton =  findViewById(R.id.registerButton);
        login =  findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),TermsAndConditions.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dialog.dismiss();
                final TextView etaddress = new TextView(RegisterActivity.this);
                //alertdialog.show();

                closeKeyboard();
                user=et_username.getText().toString();
                pass=et_password.getText().toString();
                phone=et_phone.getText().toString();
                if (pass.equals("")) {
                    et_username.setError("can't be blank");
                } else if (user.equals("")) {
                    et_password.setError("can't be blank");
                }
                else if (user.length() < 5) {
                    et_username.setError("at least 5 characters long");
                }
                else if (pass.length() < 5) {
                    et_password.setError("at least 5 characters long");
                }
                else if(phone.length()<10||phone.isEmpty()){
                    et_phone.setError("Enter valid mobile number");
                }
                else {

                    if (isNetworkAvailable(RegisterActivity.this)){

                        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(RegisterActivity.this);
                        alertdialog.setTitle("TnC Apply ");
                        alertdialog.setMessage("");
                        alertdialog.setNeutralButton("Yes, I agree to the terms", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i=new Intent(RegisterActivity.this,Phone_Auth.class);
                                i.putExtra("phone",phone);
                                i.putExtra("username",user);
                                i.putExtra("password",pass);
                                saveData();
                                startActivity(i);
                                finish();
                            }
                        });
                        alertdialog.show();

                    }
                    else
                        Toast.makeText(RegisterActivity.this,"network error",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    private void saveData() {

        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("name",user);
        editor.putString("phone",et_phone.getText().toString());
        editor.putString("password",et_password.getText().toString());

        editor.apply();

    }
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(registerButton.getWindowToken(), 0);

    }
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo()!=null
                &&connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
