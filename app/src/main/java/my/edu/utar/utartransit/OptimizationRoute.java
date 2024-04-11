package my.edu.utar.utartransit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OptimizationRoute extends AppCompatActivity implements OnMapReadyCallback {

    // Filtter by
    private ToggleButton toggleFilterBy;
    private ToggleButton toggleTransportation;
    private ToggleButton toggleOptimization;
    private LinearLayout checkboxContainer1;
    private LinearLayout checkboxContainer1_1;
    private LinearLayout checkboxContainer1_2;

    // Google map
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private SearchView mapSearchView;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    // Supabase
    private static final String SUPABASE_URL = "https://slyrebgznitqrqnzoquz.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNseXJlYmd6bml0cXJxbnpvcXV6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTI0MDg0MTIsImV4cCI6MjAyNzk4NDQxMn0.nPI7wkSGCBJHisTWvYOW1qNA8V6WnaZpssydh-l5Ugc";
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimization_route);

        // Filtter by
        toggleFilterBy = findViewById(R.id.toggleFilterBy);
        checkboxContainer1 = findViewById(R.id.checkboxContainer1);

        toggleFilterBy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxContainer1.setVisibility(View.VISIBLE);
            } else {
                checkboxContainer1.setVisibility(View.GONE);
            }
        });

        // transportation
        toggleTransportation = findViewById(R.id.toggleTransportation);
        checkboxContainer1_1 = findViewById(R.id.checkboxContainer1_1);

        toggleTransportation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxContainer1_1.setVisibility(View.VISIBLE);
            } else {
                checkboxContainer1_1.setVisibility(View.GONE);
            }
        });

        // optimization
        toggleOptimization = findViewById(R.id.toggleOptimization);
        checkboxContainer1_2 = findViewById(R.id.checkboxContainer1_2);

        toggleOptimization.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxContainer1_2.setVisibility(View.VISIBLE);
            } else {
                checkboxContainer1_2.setVisibility(View.GONE);
            }
        });

        // Google map
        mapSearchView = findViewById(R.id.mapSearch);
        mapSearchView.setQueryHint("Search...");

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null) {
                    Geocoder geocoder = new Geocoder(OptimizationRoute.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        // supabase, fetch all the table data from supabase
        fetchTableDataForBusSchedule();
        fetchTableDataForBuggySchedule();
    }

    // Google Map
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(OptimizationRoute.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        LatLng location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(location).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        //mMap.getUiSettings().setScrollGesturesEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is denied, please allow the permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Supabase
    // fetch data for bus's tables
    private void fetchTableDataForBusSchedule() {
        List<String> tableNames1 = Arrays.asList(
                "bus_schedule",
                "bus_route",
                "bus_stops",
                "bus_trips",
                "bus_trip_stop",
                "bus_period",
                "bus_additional_trip_stop",
                "bus_additional_trips",
                "bus_available_date",
                "bus_cancelled_trips"
        );

        new Thread(() -> {
            try {
                for (String tableName1 : tableNames1) {
                    String url = SUPABASE_URL + "/rest/v1/" + tableName1 + "?select=*";
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("apikey", SUPABASE_KEY)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }

                        String responseBody = response.body().string();
                        Log.d("Supabase", "Table " + tableName1 + " Data: " + responseBody);

                        // Parse responseBody using Gson into your model classes if needed
                        // Example: YourModelClass model = gson.fromJson(responseBody, YourModelClass.class);
                    }
                }
            } catch (IOException e) {
                Log.e("Supabase", "Error fetching table data: " + e.getMessage());
            }
        }).start();
    }

    // fetch data for buggy's tables
    private void fetchTableDataForBuggySchedule() {
        List<String> tableNames2 = Arrays.asList(
                "buggy_schedule",
                "buggy_stops",
                "buggy_trips",
                "buggy_trip_stop",
                "buggy_cancelled_trip",
                "buggy_is_cancelled"
        );

        new Thread(() -> {
            try {
                for (String tableName2 : tableNames2) {
                    String url = SUPABASE_URL + "/rest/v1/" + tableName2 + "?select=*";
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("apikey", SUPABASE_KEY)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }

                        String responseBody = response.body().string();
                        Log.d("Supabase", "Table " + tableName2 + " Data: " + responseBody);

                        // Parse responseBody using Gson into your model classes if needed
                        // Example: YourModelClass model = gson.fromJson(responseBody, YourModelClass.class);
                    }
                }
            } catch (IOException e) {
                Log.e("Supabase", "Error fetching table data: " + e.getMessage());
            }
        }).start();
    }
}