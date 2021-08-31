package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SharedPreferences.Editor _sharedPrefsEdit;
    SharedPreferences _appSettingPrefs;

    Boolean _isNightModeOn;
    Dialog _dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _appSettingPrefs = getSharedPreferences("AppSettingPrefs",0); // get the storage reference
        _sharedPrefsEdit = _appSettingPrefs.edit(); //set the storage editor reference

        _dialog = new Dialog(this);

        //region night mode for activity

        _isNightModeOn = _appSettingPrefs.getBoolean("NightMode",false); //check the variable status

        if(_isNightModeOn) // set the mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //night
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //day

        //endregion

        ImageButton btnLogo  = findViewById(R.id.btnlogo_welcome);
        btnLogo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChangeTheme();
            }
        });

        Button btnLogin  = findViewById(R.id.btnLogin_welcome);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowLoginPopUp();
            }
        });

        Button btnSignUp  = findViewById(R.id.btnSignup_welcome);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowSignUpPopUp();
            }
        });
    }

    void ChangeTheme() {

        if(_isNightModeOn) // variable set in OnCreate
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            _sharedPrefsEdit.putBoolean("NightMode",false);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            _sharedPrefsEdit.putBoolean("NightMode",true);
        }
        _sharedPrefsEdit.apply();
    } // called by btnLogo

    void ShowLoginPopUp(){
        _dialog.setContentView(R.layout.login_popup);

        //region Cancel
        Button btnCancel = _dialog.findViewById(R.id.btnCancel_LoginPopup);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _dialog.dismiss();
            }
        });
        //endregion

        //region Login
        Button btnDone = _dialog.findViewById(R.id.btnDone_LoginPopup);
        btnDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class); //replace with check login
                startActivity(intent);
            }
        });
        //endregion

        //region google auth
        Button btnGoogle = _dialog.findViewById(R.id.btnLoginGoogle_LoginPopup);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class); //replace with check login
                startActivity(intent);
            }
        });
        //endregion

        _dialog.show();
    }//called by btnLogin

    void ShowSignUpPopUp(){
        _dialog.setContentView(R.layout.signup_popup);

        //region Cancel
        Button btnCancel = _dialog.findViewById(R.id.btnCancel_signUpPopup);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _dialog.dismiss();
            }
        });
        //endregion

        //region Login
        Button btnDone = _dialog.findViewById(R.id.btnDone_signUpPopup);
        btnDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class); //replace with check login
                startActivity(intent);
            }
        });
        //endregion

        //region google auth
        Button btnGoogle = _dialog.findViewById(R.id.btnSignUpGoogle_SignUpPopup);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class); //replace with check login
                startActivity(intent);
            }
        });
        //endregion

        _dialog.show();

    }//called by btnSignUp
}