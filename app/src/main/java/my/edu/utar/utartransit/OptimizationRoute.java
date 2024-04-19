package my.edu.utar.utartransit;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OptimizationRoute extends Fragment implements OnMapReadyCallback {

    // Google Map
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private SearchView mapSearchView;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    // Filtter by
    private ToggleButton toggleFilterBy;
    private ToggleButton toggleTransportation;
    private LinearLayout checkboxContainer1;
    private LinearLayout checkboxContainer1_1;

    // checkbox
    private CheckBox checkboxBus;
    private CheckBox checkboxBuggy;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_optimization_route, container, false);

        // Google Map
        mapSearchView = view.findViewById(R.id.mapSearch);
        mapSearchView.setQueryHint("Search...");

        // Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Filtter by
        toggleFilterBy = view.findViewById(R.id.toggleFilterBy);
        checkboxContainer1 = view.findViewById(R.id.checkboxContainer1);

        toggleFilterBy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxContainer1.setVisibility(View.VISIBLE);
            } else {
                checkboxContainer1.setVisibility(View.GONE);
            }
        });

        // Transportation
        toggleTransportation = view.findViewById(R.id.toggleTransportation);
        checkboxContainer1_1 = view.findViewById(R.id.checkboxContainer1_1);

        toggleTransportation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxContainer1_1.setVisibility(View.VISIBLE);
            } else {
                checkboxContainer1_1.setVisibility(View.GONE);
            }
        });

        // Get references to the checkboxes
        checkboxBus = view.findViewById(R.id.checkboxBus);
        checkboxBuggy = view.findViewById(R.id.checkboxBuggy);

        // Supabase
        // Call methods to fetch data
        fetchBusStopsData();
        fetchBuggyStopsData();

        // Spinners
        spinnerDeparture = view.findViewById(R.id.spinnerDeparture);
        spinnerArrival = view.findViewById(R.id.spinnerArrival);
        departureAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, stopNames);
        arrivalAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, stopNames);

        departureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrivalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDeparture.setAdapter(departureAdapter);
        spinnerArrival.setAdapter(arrivalAdapter);

        // Set up the switchButton
        switchButton = view.findViewById(R.id.switchButton);
        switchButton.setOnClickListener(v -> {
            // Get the selected items
            String selectedDeparture = (String) spinnerDeparture.getSelectedItem();
            String selectedArrival = (String) spinnerArrival.getSelectedItem();

            // Swap selected items
            spinnerDeparture.setSelection(arrivalAdapter.getPosition(selectedArrival));
            spinnerArrival.setSelection(departureAdapter.getPosition(selectedDeparture));
        });

        // Find route button
        buttonFindRoute = view.findViewById(R.id.buttonFindRoute);
        buttonFindRoute.setOnClickListener(v -> {
            // Get the selected items from spinners
            String selectedDeparture = spinnerDeparture.getSelectedItem().toString();
            String selectedArrival = spinnerArrival.getSelectedItem().toString();

            // Get the latitude and longitude for the selected departure and arrival
            double departureLatitude = stopLatitudes.get(stopNames.indexOf(selectedDeparture));
            double departureLongitude = stopLongitudes.get(stopNames.indexOf(selectedDeparture));
            double arrivalLatitude = stopLatitudes.get(stopNames.indexOf(selectedArrival));
            double arrivalLongitude = stopLongitudes.get(stopNames.indexOf(selectedArrival));

            // Create an Intent to navigate to FindRoute activity
            Intent intent = new Intent(requireActivity(), FindRoute.class);

            // Pass selected data to the next activity
            intent.putExtra("departureName", selectedDeparture);
            intent.putExtra("departureLatitude", departureLatitude);
            intent.putExtra("departureLongitude", departureLongitude);
            intent.putExtra("arrivalName", selectedArrival);
            intent.putExtra("arrivalLatitude", arrivalLatitude);
            intent.putExtra("arrivalLongitude", arrivalLongitude);

            // Start the FindRoute activity
            startActivity(intent);
        });

        // Return the view
        return view;
    }

    // Google Map
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(requireActivity(), location -> {
            if (location != null){
                currentLocation = location;
                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Display current location with a custom marker icon (Red color)
        if (currentLocation != null) {
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            BitmapDescriptor currentLocationIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("My Location").icon(currentLocationIcon));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
        }

        // Loop to add marker for the list of stops
        for (int i = 0; i < stopNames.size(); i++){
            String stopName = stopNames.get(i);
            double latitude = stopLatitudes.get(i);
            double longitude = stopLongitudes.get(i);

            // create LatLng object for the stop location
            LatLng stopLatLng = new LatLng(latitude, longitude);

            // Create a green marker icon for stops
            BitmapDescriptor stopIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            mMap.addMarker(new MarkerOptions().position(stopLatLng).title(stopName).icon(stopIcon));
        }

        // UI settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else {
                Toast.makeText(requireContext(), "Location permission is denied, please allow the permission.", Toast.LENGTH_SHORT).show();
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
                        requireActivity().runOnUiThread(new Runnable() {
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
                        // Log the fetched data
                        Log.d("Supabase", "Stop Name: " + stopNames + ", Latitude: " + stopLatitudes + ", Longitude: " + stopLongitudes);
                        requireActivity().runOnUiThread(new Runnable() {
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

    private void updateSpinnersForTransportation() {
        // Clear existing lists
        stopNames.clear();
        stopLatitudes.clear();
        stopLongitudes.clear();

        // Fetch data based on selected checkboxes
        if (checkboxBus.isChecked()) {
            fetchBusStopsData();
        }
        if (checkboxBuggy.isChecked()) {
            fetchBuggyStopsData();
        }

        // If both checkboxes are unchecked, fetch all stops
        if (!checkboxBus.isChecked() && !checkboxBuggy.isChecked()) {
            fetchBusStopsData();
            fetchBuggyStopsData();
        }

        // Update adapters for spinners
        departureAdapter.notifyDataSetChanged();
        arrivalAdapter.notifyDataSetChanged();
    }
}