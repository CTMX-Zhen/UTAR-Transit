package my.edu.utar.utartransit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OptimizationRoute extends AppCompatActivity implements OnMapReadyCallback {

    // Google Map
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private SearchView mapSearchView;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    // Filtter by
    private ToggleButton toggleFilterBy;
    private ToggleButton toggleTransportation;
    private ToggleButton toggleOptimization;
    private LinearLayout checkboxContainer1;
    private LinearLayout checkboxContainer1_1;
    private LinearLayout checkboxContainer1_2;

    // Supabase
    private static final String SUPABASE_URL = "https://slyrebgznitqrqnzoquz.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNseXJlYmd6bml0cXJxbnpvcXV6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTI0MDg0MTIsImV4cCI6MjAyNzk4NDQxMn0.nPI7wkSGCBJHisTWvYOW1qNA8V6WnaZpssydh-l5Ugc";
    private OkHttpClient client = new OkHttpClient();

    // list for spinner
    private ArrayList<String> stopNames = new ArrayList<>();
    private ArrayList<Double> stopLatitudes = new ArrayList<>();
    private ArrayList<Double> stopLongitudes = new ArrayList<>();
    // Initialize Spinners
    private Spinner spinnerDeparture;
    private Spinner spinnerArrival;
    private ArrayAdapter<String> departureAdapter;
    private ArrayAdapter<String> arrivalAdapter;

    // switchButton
    private Button switchButton;;
    private Button buttonFindRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimization_route);

        // Google Map
        mapSearchView = findViewById(R.id.mapSearch);
        mapSearchView.setQueryHint("Search...");

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null){
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

        // Supabase
        // Call methods to fetch data
        fetchBusStopsData();
        fetchBuggyStopsData();

        // Spinners
        spinnerDeparture = findViewById(R.id.spinnerDeparture);
        spinnerArrival = findViewById(R.id.spinnerArrival);
        departureAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stopNames);
        arrivalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stopNames);

        departureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrivalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDeparture.setAdapter(departureAdapter);
        spinnerArrival.setAdapter(arrivalAdapter);

        spinnerDeparture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                //Toast.makeText(OptimizationRoute.this, "Departure Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection if needed
            }
        });

        spinnerArrival.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                //Toast.makeText(OptimizationRoute.this, "Arrival Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection if needed
            }
        });

        // Set up the switchButton
        switchButton = findViewById(R.id.switchButton);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected items
                String selectedDeparture = (String) spinnerDeparture.getSelectedItem();
                String selectedArrival = (String) spinnerArrival.getSelectedItem();

                // Swap selected items
                spinnerDeparture.setSelection(arrivalAdapter.getPosition(selectedArrival));
                spinnerArrival.setSelection(departureAdapter.getPosition(selectedDeparture));
            }
        });
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
                if (location != null){
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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else {
                Toast.makeText(this, "Location permission is denied, please allow the permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //supabase
    private void fetchBusStopsData() {
        String url = SUPABASE_URL + "/rest/v1/bus_stops?select=stop_name,latitude,longitude";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Supabase", "Error fetching bus stops data: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String stopName = jsonObject.getString("stop_name");
                            double latitude = jsonObject.getDouble("latitude");
                            double longitude = jsonObject.getDouble("longitude");
                            addUniqueStop(stopName, latitude, longitude);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                departureAdapter.notifyDataSetChanged();
                                arrivalAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("Supabase", "Error parsing bus stops JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("Supabase", "Unexpected code " + response);
                }
            }
        });
    }

    private void fetchBuggyStopsData() {
        String url = SUPABASE_URL + "/rest/v1/buggy_stops?select=stop_name,latitude,longitude";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Supabase", "Error fetching buggy stops data: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String stopName = jsonObject.getString("stop_name");
                            double latitude = jsonObject.getDouble("latitude");
                            double longitude = jsonObject.getDouble("longitude");
                            addUniqueStop(stopName, latitude, longitude);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                departureAdapter.notifyDataSetChanged();
                                arrivalAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("Supabase", "Error parsing buggy stops JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("Supabase", "Unexpected code " + response);
                }
            }
        });
    }

    // add a unique stop name to the list
    private synchronized void addUniqueStop(String stopName, double latitude, double longitude) {
        if (!stopNames.contains(stopName)) {
            stopNames.add(stopName);
        }
        if (!stopLatitudes.contains(latitude)) {
            stopLatitudes.add(latitude);
        }
        if (!stopLongitudes.contains(longitude)) {
            stopLongitudes.add(longitude);
        }
    }
}