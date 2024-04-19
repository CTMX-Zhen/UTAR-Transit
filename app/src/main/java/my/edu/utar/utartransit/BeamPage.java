package my.edu.utar.utartransit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BeamPage extends AppCompatActivity implements OnMapReadyCallback {

    // Google Map
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    private ArrayList<String> locationNames = new ArrayList<>();
    private ArrayList<Double> stopLatitudes = new ArrayList<>();
    private ArrayList<Double> stopLongitudes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam_page);

        // Initialize Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        fetchBeamData();

        TextView textView = findViewById(R.id.t2);

        // Create a SpannableString with the text
        SpannableString spannableString = new SpannableString("Click 'Here' to visit Beam!");

        // Define ClickableSpan for the word "Here"
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Define the URL to open
                String url = "https://www.ridebeam.com/my";

                // Open the URL in a web browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        };

        // Set the ClickableSpan only on the word "Here"
        int start = spannableString.toString().indexOf("Here");
        int end = start + "Here".length();
        spannableString.setSpan(clickableSpan, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the text in the TextView
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // add data
    private void fetchBeamData() {
        locationNames.add("Zone SC Car Park");
        locationNames.add("FICT (Block N)");
        locationNames.add("FIC (Block P)");
        locationNames.add("LDK Zone L (Block L)");
        locationNames.add("FBF (Parking zone near Block K)");
        locationNames.add("FBF (Parking zone near Library)");
        locationNames.add("Block F (UTRA Administration block)");
        locationNames.add("FEGT (Block E)");
        locationNames.add("FSc");
        locationNames.add("Block B");

        stopLatitudes.add(4.33664934216598);
        stopLatitudes.add(4.33883574564347);
        stopLatitudes.add(4.33895553155954);
        stopLatitudes.add(4.34203097654606);
        stopLatitudes.add(4.34156914937687);
        stopLatitudes.add(4.34045527811366);
        stopLatitudes.add(4.33952187135556);
        stopLatitudes.add(4.3385920985572);
        stopLatitudes.add(4.33860534199836);
        stopLatitudes.add(4.33605679417243);

        stopLongitudes.add(101.135465365732);
        stopLongitudes.add(101.136535913679);
        stopLongitudes.add(101.137546263275);
        stopLongitudes.add(101.14058658916);
        stopLongitudes.add(101.142899339147);
        stopLongitudes.add(101.143773092869);
        stopLongitudes.add(101.143335892823);
        stopLongitudes.add(101.144492300721);
        stopLongitudes.add(101.144505582221);
        stopLongitudes.add(101.141535686517);
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
                    mapFragment.getMapAsync(BeamPage.this);
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
        for (int i = 0; i < locationNames.size(); i++) {
            String locationName = locationNames.get(i);
            double latitude = stopLatitudes.get(i);
            double longitude = stopLongitudes.get(i);

            // create LatLng object for the stop location
            LatLng stopLatLng = new LatLng(latitude, longitude);

            // Create a purple marker icon for stops
            int markerColor = Color.parseColor("#800080");
            BitmapDescriptor stopIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
            mMap.addMarker(new MarkerOptions().position(stopLatLng).title(locationName).icon(stopIcon));
        }

        // UI settings
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
}