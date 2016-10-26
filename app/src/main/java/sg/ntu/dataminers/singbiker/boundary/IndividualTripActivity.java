package sg.ntu.dataminers.singbiker.boundary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import sg.ntu.dataminers.singbiker.IntentConstants;
import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.entity.Trip;

/*
To be implemented by Viktor.
 */

public class IndividualTripActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private int callingActivity;
    private Trip trip;
    private boolean preconditionsSatisfied;
    private GoogleMap map;
    private LatLngBounds bounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_individual_trip);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_individual_trip);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_individual_trip);
        navigationView.setNavigationItemSelectedListener(this);

        callingActivity = getIntent().getIntExtra(IntentConstants.CONSTANT_STRING_CALLINGACTIVITY, -1);
        trip = getIntent().getParcelableExtra(IntentConstants.CONSTANT_STRING_TRIP);

        TextView textView = (TextView) findViewById(R.id.individual_trip_map_textview);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.individual_trip_map_fragment);

        checkPreconditions(callingActivity, trip);

        if (preconditionsSatisfied) {
            mapFragment.getMapAsync(this);

            // Set TextView-text.
            String text = getString(R.string.individual_trip_textview_text);
            double distanceInKM = trip.getRouteCycled().getDistanceInMeters() / 1000;
            double averageSpeed = trip.getAverageSpeed();
            textView.setText(String.format(text, distanceInKM, averageSpeed));
        }
        else {
            textView.setVisibility(View.GONE);
            mapFragment.getView().setVisibility(View.GONE);

            Toast.makeText(getApplicationContext(), "Bad preconditions!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_individual_trip);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // If preconditions are satisfied.
        if (preconditionsSatisfied) {
            // Inflate menu (containing action buttons).
            getMenuInflater().inflate(R.menu.individual_trip, menu);

            // Hide action buttons that shouldn't be used.
            if (callingActivity == IntentConstants.CONSTANT_INT_ROUTEPLOTACTIVITY) {
                menu.findItem(R.id.action_remove_trip).setVisible(false);
            }
            else if (callingActivity == IntentConstants.CONSTANT_INT_HISTORYLISTACTIVITY) {
                menu.findItem(R.id.action_save_trip).setVisible(false);
                menu.findItem(R.id.action_discard_trip).setVisible(false);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save_trip) {
            Toast.makeText(getApplicationContext(), "SAVE TRIP", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.action_discard_trip) {
            Toast.makeText(getApplicationContext(), "DISCARD TRIP", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.action_remove_trip) {
            Toast.makeText(getApplicationContext(), "REMOVE TRIP", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_routeplanner) {
            intent = new Intent(getApplicationContext(), RoutePlotActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_haze) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_individual_trip);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap mapLocal) {

        map = mapLocal;

        LatLng latLngStart = new LatLng(trip.getRouteCycled().getPointStart().getLatitude(), trip.getRouteCycled().getPointStart().getLongitude());
        LatLng latLngEnd = new LatLng(trip.getRouteCycled().getPointEnd().getLatitude(), trip.getRouteCycled().getPointEnd().getLongitude());

        Marker markerStart = map.addMarker(new MarkerOptions().position(latLngStart).draggable(false));
        Marker markerEnd = map.addMarker(new MarkerOptions().position(latLngEnd).draggable(false));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(markerStart.getPosition());
        builder.include(markerEnd.getPosition());

        bounds = builder.build();

        map.setOnMapLoadedCallback(this);

        map.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public void onMapLoaded() {
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void checkPreconditions(int callingActivity, Trip trip) {

        if (callingActivity == -1) {
            preconditionsSatisfied = false;
            return;
        }
        else if (trip == null) {
            preconditionsSatisfied = false;
            return;
        }

        preconditionsSatisfied = true;
    }
}
