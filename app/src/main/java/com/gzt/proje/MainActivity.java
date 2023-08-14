package com.gzt.proje;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    public double lat, lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkNetwokConnection()) {
            Toast.makeText(this, "Unable to connect internet", Toast.LENGTH_SHORT).show();
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) { }

            @Override
            public void onProviderDisabled(@NonNull String provider) { }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
        });

    }
    public void menuClick(View view) {
        int id = view.getId();
        if(id == R.id.scan) {
            ScanOptions options = new ScanOptions();
            options.setOrientationLocked(true);
            options.setCaptureActivity(CameraActivity.class);
            launchCamera.launch(options);
        } else if(id == R.id.list) {
            Intent intent = new Intent(this, ProductListActivity.class);
            startActivity(intent);
        } else if(id == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
    }
    private ActivityResultLauncher<ScanOptions> launchCamera = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null) {
            try{
                Geocoder coder = new Geocoder(this, Locale.getDefault());
                Intent intent = new Intent(this, FinalActivity.class);
                List<Address> add = coder.getFromLocation(lat, lon, 1);
                intent.putExtra("barcode", result.getContents());
                intent.putExtra("country", add.get(0).getCountryCode().toLowerCase());
                intent.putExtra("city", add.get(0).getAdminArea());
                startActivity(intent);
            } catch(Exception e){
                Log.d("exception", e.getMessage());
            }
        }
    });
    private boolean checkNetwokConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = false;

        if(networkInfo != null && (isConnected = networkInfo.isConnected())) {}
        return isConnected;
    }
}