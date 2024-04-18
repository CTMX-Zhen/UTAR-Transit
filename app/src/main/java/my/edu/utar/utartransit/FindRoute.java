package my.edu.utar.utartransit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    // Supabase
    private static final String SUPABASE_URL = "https://slyrebgznitqrqnzoquz.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNseXJlYmd6bml0cXJxbnpvcXV6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTI0MDg0MTIsImV4cCI6MjAyNzk4NDQxMn0.nPI7wkSGCBJHisTWvYOW1qNA8V6WnaZpssydh-l5Ugc";
    private OkHttpClient client = new OkHttpClient();

    // fetch bus timetable data
    private List<String> tsIds = new ArrayList<>();
    private List<String> tsTripIds = new ArrayList<>();
    private List<String> tsStopIds = new ArrayList<>();
    private List<String> timeAtStops = new ArrayList<>();

    // Declare ArrayLists to store data based on conditions
    private List<String> stanfordCKOpticalShopTamanMahsuriImpian = new ArrayList<>();
    private List<String> tamanMahsuriImpian = new ArrayList<>();
    private List<String> harvardCambridge = new ArrayList<>();
    private List<String> harvardCambridgeWestlakeHomes = new ArrayList<>();
    private List<String> westlakeHomesI = new ArrayList<>();
    private List<String> westlakeHomesII = new ArrayList<>();
    private List<String> westlakeHomesIII = new ArrayList<>();

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

        // fetch bus timetable data
        fetchBusTripStop();
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

        // Walking speed assumed: 2.5 km/h (adjust as needed)
        double walkingSpeedKmPerHour = 2.5;

        // Calculate time in minutes based on walking speed
        double timeInHours = distanceInMeters / (walkingSpeedKmPerHour * 1000); // Distance in km / speed in km/h
        int timeInMinutes = (int) (timeInHours * 60);

        // Update TextView with estimated time of arrival
        TextView timeTextView = findViewById(R.id.time);
        timeTextView.setText(String.format("%d minutes walk", timeInMinutes));
    }

    // fetch data for bus schedule data from supabase
    //bus_trip_stop
    private void fetchBusTripStop() {
        String url = SUPABASE_URL + "/rest/v1/bus_trip_stop?select=*";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Supabase", "Error fetching bus_trip_stop data: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String tsId = jsonObject.getString("trip_stop_id");
                            String tsTripId = jsonObject.getString("trip_id");
                            String tsStopId = jsonObject.getString("stop_id");
                            String timeAtStop = jsonObject.getString("time_at_stop");
                            tsIds.add(tsId);
                            tsTripIds.add(tsTripId);
                            tsStopIds.add(tsStopId);
                            timeAtStops.add(timeAtStop);

                            populateArrayLists();
                        }

                        // Log the fetched data
                        Log.d("Supabase", "tsId" + tsIds + "trip_id: " + tsTripIds + "stop_id: " + tsStopIds + "time_at_stop: " + timeAtStops);
                    } catch (JSONException e) {
                        Log.e("Supabase", "Error parsing bus_trip_stop JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("Supabase", "Unexpected code " + response);
                }
            }
        });
    }

    private void populateArrayLists() {
        for (int i = 0; i < tsIds.size(); i++) {
            String tsId = tsIds.get(i);
            String timeAtStop = timeAtStops.get(i);
            String stopName = "";

            switch (tsId){
                //stanfordCKOpticalShopTamanMahsuriImpian
                case "1": case "6": case "10": case "14": case "18": case "23": case "27": case "32": case "36":
                    stopName = "Block D - G - N";
                    stanfordCKOpticalShopTamanMahsuriImpian.add(stopName);
                    stanfordCKOpticalShopTamanMahsuriImpian.add(timeAtStop);
                    break;
                case "2": case "7": case "19": case "28": case "37":
                    stopName = "Stanford";
                    stanfordCKOpticalShopTamanMahsuriImpian.add(stopName);
                    stanfordCKOpticalShopTamanMahsuriImpian.add(timeAtStop);
                    break;
                case "3": case "8": case "11": case "15": case "20": case "24": case "29": case "33": case "38":
                    stopName = "CK Optical Shop";
                    stanfordCKOpticalShopTamanMahsuriImpian.add(stopName);
                    stanfordCKOpticalShopTamanMahsuriImpian.add(timeAtStop);
                    break;
                case "4": case "12": case "16": case "21": case "25": case "30": case "34": case "39":
                    stopName = "Taman Mahsuri Impian";
                    stanfordCKOpticalShopTamanMahsuriImpian.add(stopName);
                    stanfordCKOpticalShopTamanMahsuriImpian.add(timeAtStop);
                    break;
                case "5": case "9": case "13": case "17": case "22": case "26": case "31": case "35": case "40":
                    stopName = "Block G - N - D";
                    stanfordCKOpticalShopTamanMahsuriImpian.add(stopName);
                    stanfordCKOpticalShopTamanMahsuriImpian.add(timeAtStop);
                    break;

                //tamanMahsuriImpian
                case "41": case "44": case "47": case "50": case "53": case "56": case "59": case "62": case "65":
                    stopName = "Block D - G - N";
                    tamanMahsuriImpian.add(stopName);
                    tamanMahsuriImpian.add(timeAtStop);
                    break;
                case "42": case "45": case "48": case "51": case "54": case "57": case "60": case "63": case "66":
                    stopName = "Taman Mahsuri Impian";
                    tamanMahsuriImpian.add(stopName);
                    tamanMahsuriImpian.add(timeAtStop);
                    break;
                case "43": case "46": case "49": case "52": case "55": case "58": case "61": case "64": case "67":
                    stopName = "Block G - N - D";
                    tamanMahsuriImpian.add(stopName);
                    tamanMahsuriImpian.add(timeAtStop);
                    break;

                //harvardCambridge
                case "68": case "70": case "71":
                    stopName = "Block D";
                    harvardCambridge.add(stopName);
                    harvardCambridge.add(timeAtStop);
                    break;
                case "74": case "77": case "80": case "83": case "86": case "89": case "92": case "95": case "98": case "101":
                    stopName = "Block D - G - N";
                    harvardCambridge.add(stopName);
                    harvardCambridge.add(timeAtStop);
                    break;
                case "69": case "72": case "75": case "81": case "84": case "87": case "90": case "93": case "96": case "99": case "102":
                    stopName = "Harvard and Cambridge";
                    harvardCambridge.add(stopName);
                    harvardCambridge.add(timeAtStop);
                    break;
                case "73": case "76": case "79": case "82": case "85": case "88": case "91": case "94": case "97": case "100": case "103":
                    stopName = "Block N - G - D";
                    harvardCambridge.add(stopName);
                    harvardCambridge.add(timeAtStop);
                    break;

                //harvardCambridgeWestlakeHomes
                case "104":
                    stopName = "Block D";
                    harvardCambridgeWestlakeHomes.add(stopName);
                    harvardCambridgeWestlakeHomes.add(timeAtStop);
                    break;
                case "108": case "112": case "244": case "116": case "120": case "124": case "128": case "132": case "137": case "141":
                    stopName = "Block D - G - N";
                    harvardCambridgeWestlakeHomes.add(stopName);
                    harvardCambridgeWestlakeHomes.add(timeAtStop);
                    break;
                case "105": case "109": case "113": case "245": case "117": case "121": case "125": case "129": case "133": case "138": case "142":
                    stopName = "Harvard and Cambridge";
                    harvardCambridgeWestlakeHomes.add(stopName);
                    harvardCambridgeWestlakeHomes.add(timeAtStop);
                    break;
                case "106": case "110": case "114": case "246": case "118": case "122": case "126": case "130": case "134": case "139": case "143":
                    stopName = "Westlake Homes";
                    harvardCambridgeWestlakeHomes.add(stopName);
                    harvardCambridgeWestlakeHomes.add(timeAtStop);
                    break;
                case "107": case "111": case "247": case "115": case "119": case "123": case "127": case "131": case "136": case "140": case "144":
                    stopName = "Block N - G - D";
                    harvardCambridgeWestlakeHomes.add(stopName);
                    harvardCambridgeWestlakeHomes.add(timeAtStop);
                    break;

                //westlakeHomesI
                case "145": case "147": case "148":
                    stopName = "Block D";
                    westlakeHomesI.add(stopName);
                    westlakeHomesI.add(timeAtStop);
                    break;
                case "151": case "154": case "157": case "160": case "163": case "166": case "169": case "172": case "175":
                    stopName = "Block D - G - N";
                    westlakeHomesI.add(stopName);
                    westlakeHomesI.add(timeAtStop);
                    break;
                case "181": case "184": case "187": case "190": case "193": case "196": case "199": case "202": case "205": case "208":
                    stopName = "Block D - G - N";
                    westlakeHomesI.add(stopName);
                    westlakeHomesI.add(timeAtStop);
                    break;
                case "150": case "153": case "156": case "159": case "162": case "165": case "168": case "171": case "174": case "177":
                    stopName = "Block N - G - D";
                    westlakeHomesI.add(stopName);
                    westlakeHomesI.add(timeAtStop);
                    break;

                //westlakeHomesII
                case "178":
                    stopName = "Block D";
                    westlakeHomesII.add(stopName);
                    westlakeHomesII.add(timeAtStop);
                    break;
                case "179": case "182": case "185": case "188": case "191": case "194": case "197": case "200": case "203": case "206": case "209":
                    stopName = "Westlake Homes";
                    westlakeHomesII.add(stopName);
                    westlakeHomesII.add(timeAtStop);
                    break;
                case "146": case "149": case "152": case "155": case "158": case "161": case "164": case "167": case "170": case "173": case "176":
                    stopName = "Westlake Homes";
                    westlakeHomesII.add(stopName);
                    westlakeHomesII.add(timeAtStop);
                    break;
                case "180": case "183": case "186": case "189": case "192": case "195": case "198": case "201": case "204": case "207": case "210":
                    stopName = "Block N - G - D";
                    westlakeHomesII.add(stopName);
                    westlakeHomesII.add(timeAtStop);
                    break;

                //westlakeHomesIII
                case "211":
                    stopName = "Block D";
                    westlakeHomesIII.add(stopName);
                    westlakeHomesIII.add(timeAtStop);
                    break;
                case "214": case "217": case "220": case "223": case "226": case "229": case "232": case "235": case "238": case "241":
                    stopName = "Block D - G - N";
                    westlakeHomesIII.add(stopName);
                    westlakeHomesIII.add(timeAtStop);
                    break;
                case "212": case "215": case "218": case "221": case "224": case "227": case "230": case "233": case "236": case "239": case "242":
                    stopName = "Westlake Homes";
                    westlakeHomesIII.add(stopName);
                    westlakeHomesIII.add(timeAtStop);
                    break;
                case "213": case "216": case "219": case "222": case "225": case "228": case "231": case "234": case "237": case "240": case "243":
                    stopName = "Block N - G - D";
                    westlakeHomesIII.add(stopName);
                    westlakeHomesIII.add(timeAtStop);
                    break;
            }
        }

        // log the categorized data for debugging or verification
        //Log.d("Supabase", "StanfordCKOpticalShopTamanMahsuriImpian: " + stanfordCKOpticalShopTamanMahsuriImpian);
        //Log.d("Supabase", "TamanMahsuriImpian: " + tamanMahsuriImpian);
        //Log.d("Supabase", "HarvardCambridge: " + harvardCambridge);
        //Log.d("Supabase", "HarvardCambridgeWestlakeHomes: " + harvardCambridgeWestlakeHomes);
        //Log.d("Supabase", "WestlakeHomesI: " + westlakeHomesI);
        //Log.d("Supabase", "WestlakeHomesII: " + westlakeHomesII);
        //Log.d("Supabase", "WestlakeHomesIII: " + westlakeHomesIII);
    }
}