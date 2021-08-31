package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SharedPreferences.Editor _sharedPrefsEdit;
    SharedPreferences _appSettingPrefs;
    Boolean _isNightModeOn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btnLogo  = findViewById(R.id.btnlogo_welcome);
        _appSettingPrefs = getSharedPreferences("AppSettingPrefs",0); // get the storage reference
        _sharedPrefsEdit = _appSettingPrefs.edit(); //set the storage editor reference


        //night mode for activity

        _isNightModeOn = _appSettingPrefs.getBoolean("NightMode",false); //check the variable status

        if(_isNightModeOn) // set the mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //night
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //day

        //night mode for activity

        btnLogo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChangeTheme();
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
    } // called by btnLogin

}