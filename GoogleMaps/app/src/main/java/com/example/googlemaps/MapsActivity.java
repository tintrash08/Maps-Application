    package com.example.googlemaps;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
//import android.support.v4.app.FragmentActivity;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    //--------------
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String currentLatitude, currentLongitude;
    Location startPoint, endPoint;
    Marker endPointMarker;
    Button btnCalDist;
    //--------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        btnCalDist = findViewById(R.id.buttonCalDist);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }

        startPoint = new Location("Location A");
        endPoint = new Location("Location B");
        startPoint.setLatitude(Double.valueOf(currentLatitude));
        startPoint.setLatitude(Double.valueOf(currentLongitude));

        endPoint.setLatitude(Double.valueOf(currentLatitude));
        endPoint.setLatitude(Double.valueOf(currentLongitude));

        CalculateDistance(startPoint,endPoint);


        btnCalDist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                startPoint.setLatitude(Double.valueOf(currentLatitude));
                startPoint.setLatitude(Double.valueOf(currentLongitude));
                CalculateDistance(startPoint,endPoint);
            }
        });



    }
    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                currentLatitude = String.valueOf(locationGPS.getLatitude());
                currentLongitude = String.valueOf(locationGPS.getLongitude());
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(this, "Latitude: "+currentLatitude, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Longitude: "+currentLongitude, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng currentLoc;
        // Add a marker in Sydney and move the camera
        if (currentLatitude != null && currentLongitude != null){
            currentLoc = new LatLng(Double.valueOf(currentLatitude), Double.valueOf(currentLongitude));
        }
        else {
            currentLoc = new LatLng(-34, 151);
        }
        mMap.addMarker(new MarkerOptions().position(currentLoc).title("MyLocation"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,18));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                if (endPointMarker!=null){
                    endPointMarker.remove();
                }
                endPointMarker = googleMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title("Your marker title")
                        .snippet("Your marker snippet"));
                endPoint.setLatitude(point.latitude);
                endPoint.setLongitude(point.longitude);
            }
        });
    }

    void CalculateDistance(Location startPoint, Location endPoint){
        double distance=startPoint.distanceTo(endPoint)/1000;
        Toast.makeText(this, "Distance: "+distance, Toast.LENGTH_SHORT).show();
    }
}