package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    GoogleMap _map;
    SupportMapFragment _mapFragment;

    Location _currentLocation;

    Address _address;

    Boolean  _showCurrentLocation = false;

    BottomSheetBehavior _location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        _mapFragment  = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_home);

        LinearLayout linearLayout = findViewById(R.id.ll_standard_slideUpMenu);
        _location = BottomSheetBehavior.from(linearLayout);
        _location.setState(BottomSheetBehavior.STATE_HIDDEN);

        /*SearchView searchView  = findViewById(R.id.svSearch_home);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(HomeActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(latLng);
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

        _mapFragment.getMapAsync(this::onMapReady);

        ImageButton btnCurrentLocation  = findViewById(R.id.btn_CurrentLocation_home);
        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowCurrentLocation();
            }
        });


    }

//region Current Location
    void ShowCurrentLocation(){
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation(fusedLocationProviderClient);

    }
    void getCurrentLocation(FusedLocationProviderClient fusedLocationProviderClient) {
        _showCurrentLocation = true;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
                return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location !=null){
                    _currentLocation = location;
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
            case 101:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && _showCurrentLocation)
                {
                    FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                    getCurrentLocation(fusedLocationProviderClient);
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    //endregion Current Location


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        _map = googleMap;

        _map.setOnMarkerClickListener(this::onMarkerClick);
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        _location.setState(BottomSheetBehavior.STATE_COLLAPSED);

        return false;
    }
}