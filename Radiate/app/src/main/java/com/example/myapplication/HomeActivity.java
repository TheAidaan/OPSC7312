package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback {
Boolean _showCurrentLocation;
    Location _currentLocation;
    FusedLocationProviderClient _fusedLocationProviderClient;
    static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        ImageButton btnCurrentLocation  = findViewById(R.id.btn_CurrentLocation_Home);
        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowCurrentLocation();
            }
        });


    }

    void ShowCurrentLocation(){
        _showCurrentLocation = true;
        _fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        FetchLastLocation();
    }
    void FetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
                return;
        }
        Task<Location> task = _fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location !=null){
                    _currentLocation = location;
                    Toast.makeText(getApplicationContext(),_currentLocation.getLatitude() + " " + _currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_Home);
                    supportMapFragment.getMapAsync(HomeActivity.this::onMapReady);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng LatLng;
        if (_showCurrentLocation)
            LatLng = new LatLng(_currentLocation.getLatitude(), _currentLocation.getLongitude());
        else
            LatLng = new LatLng(-33.91772,18.4159913);

        //MarkerOptions markerOptions = new MarkerOptions().position(LatLng).title("here");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng));
        //googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
                   case REQUEST_CODE:
                       if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && _showCurrentLocation)
                          FetchLastLocation();
                       break;
               }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}