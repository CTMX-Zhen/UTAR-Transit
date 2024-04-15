package my.edu.utar.utartransit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

public class FindRoute extends AppCompatActivity implements OnMapReadyCallback {
    // retrieve data
    private String departureName;
    private double departureLatitude;
    private double departureLongitude;
    private String arrivalName;
    private double arrivalLatitude;
    private double arrivalLongitude;

    // Google Map
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    // calculate the distance and time
    private LatLng departureLatLng;
    private LatLng arrivalLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_route);

        // Retrieve data from the intent
        Intent intent = getIntent();
        if (intent != null) {
            departureName = intent.getStringExtra("departureName");
            departureLatitude = intent.getDoubleExtra("departureLatitude", 0.0);
            departureLongitude = intent.getDoubleExtra("departureLongitude", 0.0);
            arrivalName = intent.getStringExtra("arrivalName");
            arrivalLatitude = intent.getDoubleExtra("arrivalLatitude", 0.0);
            arrivalLongitude = intent.getDoubleExtra("arrivalLongitude", 0.0);
        }

        // Set the text for departureStopName and arrivalStopName TextViews
        TextView departureStopTextView = findViewById(R.id.departureStopName);
        TextView arrivalStopTextView = findViewById(R.id.arrivalStopName);

        if (departureName != null && !departureName.isEmpty()) {
            departureStopTextView.setText(departureName);
        }

        if (arrivalName != null && !arrivalName.isEmpty()) {
            arrivalStopTextView.setText(arrivalName);
        }

        // Initialize Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }


    // Google Map
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        // Define custom marker icons
        BitmapDescriptor departureIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED); // Red marker for departure (why red because it probably might be current location)
        BitmapDescriptor arrivalIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN); // Green marker for arrival

        // Add markers for departure and arrival locations
        departureLatLng = new LatLng(departureLatitude, departureLongitude);
        mMap.addMarker(new MarkerOptions().position(departureLatLng).title("Departure: " + departureName).icon(departureIcon));

        arrivalLatLng = new LatLng(arrivalLatitude, arrivalLongitude);
        mMap.addMarker(new MarkerOptions().position(arrivalLatLng).title("Arrival: " + arrivalName).icon(arrivalIcon));

        // Move camera to show both markers
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(departureLatLng);
        builder.include(arrivalLatLng);
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        // UI settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        //mMap.getUiSettings().setScrollGesturesEnabled(true);

        // Draw dotted line between departure and arrival
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(departureLatLng, arrivalLatLng)
                .width(5) // Set the width of the polyline
                .color(Color.BLACK) // Set the color of the polyline
                .pattern(Arrays.asList(new Dash(30), new Gap(20))); // Set the pattern for the dotted line

        mMap.addPolyline(polylineOptions);

        // Calculate and display distance and time
        updateDistanceAndTime();
    }

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
                    mapFragment.getMapAsync(FindRoute.this);
                }
            }
        });
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

    // calculate the distance and time
    private void updateDistanceAndTime() {
        Location departureLocation = new Location("departure");
        departureLocation.setLatitude(departureLatLng.latitude);
        departureLocation.setLongitude(departureLatLng.longitude);

        Location arrivalLocation = new Location("arrival");
        arrivalLocation.setLatitude(arrivalLatLng.latitude);
        arrivalLocation.setLongitude(arrivalLatLng.longitude);

        // Calculate distance in meters
        float distanceInMeters = departureLocation.distanceTo(arrivalLocation);

        if (distanceInMeters < 1000) {
            // Display distance in meters if less than 1 km
            TextView distanceTextView = findViewById(R.id.distance);
            distanceTextView.setText(String.format("%.0f meters", distanceInMeters));
        } else {
            // Display distance in kilometers with two decimal places
            double distanceInKm = distanceInMeters / 1000.0;
            TextView distanceTextView = findViewById(R.id.distance);
            distanceTextView.setText(String.format("%.2f km", distanceInKm));
        }

        // Walking speed assumed: 5 km/h (adjust as needed)
        double walkingSpeedKmPerHour = 5.0;

        // Calculate time in minutes based on walking speed
        double timeInHours = distanceInMeters / (walkingSpeedKmPerHour * 1000); // Distance in km / speed in km/h
        int timeInMinutes = (int) (timeInHours * 60);

        // Update TextView with estimated time of arrival
        TextView timeTextView = findViewById(R.id.time);
        timeTextView.setText(String.format("%d minutes walk", timeInMinutes));
    }
}