package my.edu.utar.utartransit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class OptimizationRoute extends AppCompatActivity implements OnMapReadyCallback {

    //Filtter by:
    private ToggleButton toggleFilterBy;
    private ToggleButton toggleTransportation;
    private ToggleButton toggleOptimization;
    private LinearLayout checkboxContainer1;
    private LinearLayout checkboxContainer1_1;
    private LinearLayout checkboxContainer1_2;

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private SearchView mapSearchView;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimization_route);

        //Filtter by:
        toggleFilterBy = findViewById(R.id.toggleFilterBy);
        checkboxContainer1 = findViewById(R.id.checkboxContainer1);

        toggleFilterBy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxContainer1.setVisibility(View.VISIBLE);
            } else {
                checkboxContainer1.setVisibility(View.GONE);
            }
        });

        //transportation
        toggleTransportation = findViewById(R.id.toggleTransportation);
        checkboxContainer1_1 = findViewById(R.id.checkboxContainer1_1);

        toggleTransportation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxContainer1_1.setVisibility(View.VISIBLE);
            } else {
                checkboxContainer1_1.setVisibility(View.GONE);
            }
        });

        //optimization
        toggleOptimization = findViewById(R.id.toggleOptimization);
        checkboxContainer1_2 = findViewById(R.id.checkboxContainer1_2);

        toggleOptimization.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxContainer1_2.setVisibility(View.VISIBLE);
            } else {
                checkboxContainer1_2.setVisibility(View.GONE);
            }
        });

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
}