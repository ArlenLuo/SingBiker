package sg.ntu.dataminers.singbiker.boundary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import sg.ntu.dataminers.singbiker.IntentConstants;
import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.control.MapManager;
import sg.ntu.dataminers.singbiker.control.SettingsManager;
import sg.ntu.dataminers.singbiker.entity.Route;
import sg.ntu.dataminers.singbiker.entity.Settings;
import sg.ntu.dataminers.singbiker.entity.Trip;

import static sg.ntu.dataminers.singbiker.R.menu.trip;

public class TripActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, SeekBar.OnSeekBarChangeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int distanceInMetersToDestinationFinished = 20;
    private static final int updateRatePreferred = 10000;
    private static final int updateRateFastest = 5000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private GoogleMap map;
    private LatLngBounds bounds;
    private Marker markerCurrent;
    private LatLng userCurrentPosition;
    private Route systemGeneratedRoute;
    private Trip currentTrip;
    private boolean userCycling = false;
    private boolean userHasStarted = false;
    private MenuItem infoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_trip);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_trip);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_trip);
        navigationView.setNavigationItemSelectedListener(this);

        systemGeneratedRoute = getIntent().getParcelableExtra(IntentConstants.CONSTANT_STRING_ROUTE);
        currentTrip = new Trip(systemGeneratedRoute);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.trip_map_fragment);
        mapFragment.getMapAsync(this);

        SeekBar seekBar = (SeekBar) findViewById(R.id.trip_seekbar);
        seekBar.setOnSeekBarChangeListener(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }

        // Create locationRequest with the settings the app wants.
        locationRequest = createLocationRequest();

        // Get current settings.
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Prompt the user to change location settings if needed.
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequest);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult result) {

                final Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            status.startResolutionForResult(TripActivity.this, 1001);
                        } catch (IntentSender.SendIntentException sie) {
                            sie.printStackTrace();
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_trip);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(trip, menu);

        infoButton = menu.findItem(R.id.action_trip_info);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_trip_info) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            String text = getString(R.string.trip_dialog_message);
            double distance = currentTrip.getTotalDistanceCycled();
            double averageSpeed = currentTrip.getAverageSpeed();

            if (Settings.isUnitSystemMetric()) {
                text = String.format(text, distance, averageSpeed, "km", "km/h");
            }
            else {
                distance = SettingsManager.kmToMile(distance);
                averageSpeed = SettingsManager.kmhToMph(averageSpeed);
                text = String.format(text, distance, averageSpeed, "miles", "mph");
            }

            alertDialogBuilder.setTitle(R.string.trip_dialog_title);
            alertDialogBuilder.setMessage(text);

            alertDialogBuilder.setNegativeButton(R.string.trip_dialog_button_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if (id == R.id.action_change_route) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_routeplanner) {
            intent = new Intent(getApplicationContext(), RoutePlotActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_history) {
            intent = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_haze) {

        } else if (id == R.id.nav_settings) {
            intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_trip);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap mapLocal) {
        map = mapLocal;

        LatLng latLngStart = systemGeneratedRoute.getPointStart();
        LatLng latLngEnd = systemGeneratedRoute.getPointEnd();

        Marker markerStart = map.addMarker(new MarkerOptions().position(latLngStart).draggable(false));
        Marker markerEnd = map.addMarker(new MarkerOptions().position(latLngEnd).draggable(false));

        MapManager.drawRoute(map, systemGeneratedRoute, Settings.getColorPCN());

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(markerStart.getPosition());
        builder.include(markerEnd.getPosition());

        for (LatLng waypoint : systemGeneratedRoute.getWaypoints()) {
            builder.include(waypoint);
        }

        bounds = builder.build();

        map.setOnMapLoadedCallback(this);

        map.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public void onMapLoaded() {
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if ((progress == 100) && (!userCycling)) {

            if (!userHasStarted) {
                currentTrip.beginCycling(userCurrentPosition);
                markerCurrent = map.addMarker(new MarkerOptions().position(userCurrentPosition).draggable(false).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trip_person_pin)));
                userHasStarted = true;
            }
            else {
                currentTrip.continueCycling();
            }

            startLocationUpdates();

            Toast.makeText(getApplicationContext(), "Start!", Toast.LENGTH_SHORT).show();
            infoButton.setVisible(false);
            userCycling = true;
        }
        else if ((progress == 0) && (userCycling)) {

            float[] results = new float[3];

            LatLng pos = currentTrip.getRouteCycled().getPointEnd();
            LatLng target = currentTrip.getRouteSystemGenerated().getPointEnd();

            Location.distanceBetween(pos.latitude, pos.longitude, target.latitude, target.longitude, results);
            float distanceToDestinaition = results[0];

            if (distanceToDestinaition < distanceInMetersToDestinationFinished) {
                currentTrip.finishedCycling(userCurrentPosition);

                stopLocationUpdates();

                Intent intent = new Intent(getApplicationContext(), IndividualTripActivity.class);
                intent.putExtra(IntentConstants.CONSTANT_STRING_CALLINGACTIVITY, IntentConstants.CONSTANT_INT_TRIPACTIVITY);
                intent.putExtra(IntentConstants.CONSTANT_STRING_TRIP, currentTrip);
                startActivity(intent);
            }
            else {
                currentTrip.pauseCycling(userCurrentPosition);
            }

            stopLocationUpdates();

            Toast.makeText(getApplicationContext(), "Stop!", Toast.LENGTH_SHORT).show();
            infoButton.setVisible(true);
            userCycling = false;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            userCurrentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Client is temporary disconnected!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Couldn't connect client to service!", Toast.LENGTH_SHORT).show();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setInterval(updateRatePreferred);
        locationRequest.setFastestInterval(updateRateFastest);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    private void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {

        userCurrentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        currentTrip.updateRouteCycled(userCurrentPosition);

        markerCurrent.setPosition(userCurrentPosition);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(markerCurrent.getPosition());
        builder.include(currentTrip.getRouteSystemGenerated().getPointEnd());
        bounds = builder.build();
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(userCurrentPosition, 17));

        MapManager.drawRoute(map, currentTrip.getRouteCycled(), Settings.getColorNonPCN());
    }
}
