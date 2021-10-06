package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap _map;
    SupportMapFragment _mapFragment;

    FusedLocationProviderClient _fusedLocationProviderClient;
    static final int REQUEST_CODE = 101;
    Location _currentLocation;

    SearchView _searchView;
    Address _address;

    Boolean  _showCurrentLocation = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        _mapFragment  = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_home);
        _mapFragment.getMapAsync(this::onMapReady);

        /*_searchView  = findViewById(R.id.svSearch_home);
        _searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = _searchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(HomeActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    _map.addMarker(new MarkerOptions().position(latLng).title(location));
                    _map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/




        ImageButton btnCurrentLocation  = findViewById(R.id.btn_CurrentLocation_home);
        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowCurrentLocation();
            }
        });


    }

//region Current Location
    void ShowCurrentLocation(){
        _fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        FetchLastLocation();

    }
    void FetchLastLocation() {
        _showCurrentLocation = true;
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
                    Toast.makeText(getApplicationContext(),location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_home);
                    supportMapFragment.getMapAsync(HomeActivity.this::onMapReady);
                    _map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),10));
                    _showCurrentLocation = false;
                }
            }
        });
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

    //endregion Current Location


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        _map = googleMap;
    }


}