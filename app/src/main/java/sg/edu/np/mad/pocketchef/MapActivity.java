package sg.edu.np.mad.pocketchef;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.maps.android.PolyUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 444;
    private GoogleMap mMap;
    private boolean locationPermissionGranted;
    FusedLocationProviderClient fusedLocationClient;
    LocationManager locationManager;
    Location lastKnownLocation;
    private OkHttpClient client;

    double latitude;
    double longitude;
    String name;
    ImageView backIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        backIv = findViewById(R.id.backIv);
        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
        name = getIntent().getStringExtra("Name");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        client = new OkHttpClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backIv.setOnClickListener(v -> onBackPressed());

    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocationPermission();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            checkGPSEnabled();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                checkGPSEnabled();
            }
        }
    }

    private void checkGPSEnabled() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getDeviceLocation();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("GPS is disabled. Please enable GPS to use this app.")
                    .setCancelable(false)
                    .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 123);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
        }
    }


    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            lastKnownLocation = task.getResult();
                            double la = lastKnownLocation.getLatitude();
                            double lo = lastKnownLocation.getLongitude();
                            Toast.makeText(MapActivity.this, "Location Loaded", Toast.LENGTH_SHORT).show();
                            drawRoute(new LatLng(la, lo), new LatLng(latitude, longitude));

                        } else {
                            Toast.makeText(MapActivity.this, "Location not loaded!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);

    }

    private void drawRoute(LatLng start, LatLng end) {

        mMap.addMarker(new MarkerOptions().position(start).title("Start Location"));
        mMap.addMarker(new MarkerOptions().position(end).title("End Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 10));


        Toast.makeText(this, "Drawing Routes", Toast.LENGTH_SHORT).show();

        String apiKey = getString(R.string.s_map_api);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                start.latitude + "," + start.longitude + "&destination=" +
                end.latitude + "," + end.longitude + "&key=" + apiKey;

        Log.d("Directions API URL", url);

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("Directions API Error", e.getMessage());
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("Directions API Response", responseData);
                    parseAndDrawRoute(responseData);
                } else {
                    Log.e("Directions API Error", response.toString());
                    runOnUiThread(() -> Toast.makeText(MapActivity.this, "Error: " + response.toString(), Toast.LENGTH_SHORT).show());
                }
            }

        });
    }

    private void parseAndDrawRoute(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray routes = jsonObject.getJSONArray("routes");
            if (routes.length() > 0) {
                JSONObject route = routes.getJSONObject(0);
                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                String encodedPolyline = overviewPolyline.getString("points");
                List<LatLng> decodedPath = PolyUtil.decode(encodedPolyline);

                runOnUiThread(() -> {
                    mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(R.color.paletteorange).width(5));
                });
            } else {
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "No routes found", Toast.LENGTH_SHORT).show());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(MapActivity.this, "Parsing error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}