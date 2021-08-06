package com.example.susa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.susa.database.requests.RequestTaskRunner;
import com.example.susa.mapUtils.GetDirection;
import com.example.susa.models.User;
import com.example.susa.models.jsonmodels.UserJson;
import com.example.susa.utils.AndroidUtil;
import com.example.susa.utils.SearchAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RequestTaskRunner.Callback<String> {

    private static GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private static SearchView searchBar;
    private SearchAdapter adapter;
    private static RecyclerView recyclerView;
    private ImageView menuIV;

    private LatLng destPoint;
    private LatLng userLocation;


    private boolean zoomed = false;


    final int LOCATION_REQUEST_CODE = 1;
    private final int INTERVAL = 1000; //user location update time interval
    private final int FASTEST_INTERVAL = 1000;
    private final String TAG = "SportsmanMapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                updateUI();
            }
        };
        Log.d(TAG, "onCreate...............");
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        buildLocationRequest();
        buildGoogleApiClient();

        setContentView(R.layout.activity_sportsman_map);
        ArrayList<String> dataset = new ArrayList<>();
        dataset.add("Спортивная площадка");
        dataset.add("Стадион");
        dataset.add("Дворец спорта");
        dataset.add("Крытый манеж");

        adapter = new SearchAdapter(dataset);
        searchBar = findViewById(R.id.search_bar);
        searchBar.setImeOptions(EditorInfo.IME_ACTION_DONE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.setVisibility(View.INVISIBLE);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recyclerView.setVisibility(View.INVISIBLE);
//                AndroidUtil.hideKeyboard(MapActivity.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                recyclerView.setVisibility(View.VISIBLE);
                return false;
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        menuIV = findViewById(R.id.toolbar_menu);
        menuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


    }

    private void updateUI() {

        if (mLastLocation != null) {
            if (!zoomed) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15f));
                zoomed = true;
            }
            String lat = String.valueOf(mLastLocation.getLatitude());
            String lng = String.valueOf(mLastLocation.getLongitude());

        } else {
            Log.d(TAG, "Location is null");
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                BottomSheetDialog dialog = new BottomSheetDialog(
                        MapActivity.this, R.style.SlideUpPanelTheme
                );
                View slideUpPanelView = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.activity_slide_up_panel,
                        findViewById(R.id.slideUpPanel)
                );
                slideUpPanelView.findViewById(R.id.makeRouteBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = getMapUrl(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), destPoint, "drive");
                        new GetDirection(url, mMap).execute();
                        dialog.dismiss();
                    }
                });

                slideUpPanelView.findViewById(R.id.objectInfoBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MapActivity.this, ObjectInfoActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                });

                slideUpPanelView.findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(slideUpPanelView);
                dialog.show();
                return true;
            }
        });

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                recyclerView.setVisibility(View.INVISIBLE);
                AndroidUtil.hideKeyboard(MapActivity.this);
            }


        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                recyclerView.setVisibility(View.INVISIBLE);
                mMap.clear();
                Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                destPoint = latLng;
                BottomSheetDialog dialog = new BottomSheetDialog(
                        MapActivity.this, R.style.SlideUpPanelTheme
                );
                View slideUpPanelView = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.activity_slide_up_panel,
                        findViewById(R.id.slideUpPanel)
                );
                slideUpPanelView.findViewById(R.id.makeRouteBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = getMapUrl(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), destPoint, "drive");
                        new GetDirection(url, mMap).execute();
                        dialog.dismiss();
                    }
                });

                slideUpPanelView.findViewById(R.id.objectInfoBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MapActivity.this, ObjectInfoActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                        return;
                    }
                });

                slideUpPanelView.findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

//                View slideUpPanelView = LayoutInflater.from(getApplicationContext()).inflate(
//                        R.layout.activity_slide_up_admin_panel,
//                        findViewById(R.id.slideUpAdminPanel)
//                );
//                slideUpPanelView.findViewById(R.id.createObject).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(MapActivity.this, AddBuildingActivity.class);
//                        startActivity(intent);
//                        finish();
//                        dialog.dismiss();
//                        return;
//                    }
//                });
//
//                slideUpPanelView.findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });

//                View slideUpPanelView = LayoutInflater.from(getApplicationContext()).inflate(
//                        R.layout.activity_slide_up_admin_panel_on_marker_click,
//                        findViewById(R.id.slideUpOnMarkerClickAdminPanel)
//                );
//                slideUpPanelView.findViewById(R.id.editObject).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(MapActivity.this, AddBuildingActivity.class);
//                        startActivity(intent);
//                        finish();
//                        dialog.dismiss();
//                        return;
//                    }
//                });
//
//                slideUpPanelView.findViewById(R.id.deleteObject).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//                slideUpPanelView.findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
                dialog.setContentView(slideUpPanelView);
                dialog.show();
            }
        });
    }


    /**
     * User location request builder
     */
    protected void buildLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Check if google play services ar available
     *
     * @return
     */
    private boolean isGooglePlayServicesAvailable() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status == ConnectionResult.SUCCESS) {
            return true;
        } else {
            Objects.requireNonNull(GoogleApiAvailability.getInstance().getErrorDialog(this, status, 0)).show();
            return false;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient.isConnected()) {
            startLocationUpdates();

            Log.d(TAG, " Location update resumed");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
    }

    protected void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLastLocation = location;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("Hello");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000); //interval of location request
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // High accuracy drains a lot of battery power

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Permissions required");
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            System.out.println("Permissions are ok");
            mapFragment.getMapAsync(this);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onComplete(String result) {
        System.out.println(result);
        Gson gson = new Gson();
        UserJson userJson = gson.fromJson(result, UserJson.class);
        if (userJson.success) {
            User.logoutUser();
            Intent intent = new Intent(MapActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(MapActivity.this, userJson.error, Toast.LENGTH_SHORT).show();
        }

    }

    private String getMapUrl(LatLng origin, LatLng dest, String directionMode) {
        String originStr = "origin=" + origin.latitude + "," + origin.longitude;
        String destStr = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=" + directionMode;
        String key = "key=AIzaSyC9EB-Zfu4N1gQ3ecNxdSpsg5De6r4_H-A";
        String params = originStr + "&" + destStr + "&" + sensor + "&" + mode + "&" + key;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params;
    }

    public static void fillSearchBar(CharSequence string) {

        recyclerView.setVisibility(View.INVISIBLE);
        searchBar.setQuery(string, true);
    }

    public static void placeMarker(LatLng latLng){
        mMap.addMarker(new MarkerOptions().position(latLng));
    }


}