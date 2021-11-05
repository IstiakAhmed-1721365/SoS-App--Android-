/**
 * Initialize and track change in location
 * Created by Istiak Ahmed
 */
package com.example.sstto.sos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;

public class GPSLocation extends AppCompatActivity implements LocationListener {
    MainActivity mainActivity;

    /*constructor that will call the getLocation method*/
    GPSLocation(MainActivity mainActivity){
        this.mainActivity=mainActivity;
        getLocation();
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        LocationManager locationManager = (LocationManager) mainActivity.getSystemService(LOCATION_SERVICE);
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mainActivity.latitude = location.getLatitude();
            mainActivity.longitude = location.getLongitude();
        }
        /*if GPS is off*/
        else if (locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            mainActivity.latitude = location.getLatitude();
            mainActivity.longitude = location.getLongitude();
        }
        else {

            onProviderDisabled(" please turn on wifi or GPS");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mainActivity.latitude = location.getLatitude();
        mainActivity.longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(mainActivity,s,Toast.LENGTH_LONG).show();
    }
}