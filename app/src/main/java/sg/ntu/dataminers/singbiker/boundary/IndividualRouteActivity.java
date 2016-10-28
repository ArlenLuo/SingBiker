package sg.ntu.dataminers.singbiker.boundary;

import android.content.Intent;
import android.graphics.Color;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Random;

import sg.ntu.dataminers.singbiker.IntentConstants;
import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.control.MapManager;
import sg.ntu.dataminers.singbiker.entity.Route;

public class IndividualRouteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener, AdapterView.OnItemClickListener {

    private static final int maxNumberOfListItemsDisplayed = 4;
    private GoogleMap map;
    private ListView listView;
    private ArrayList<Route> listOfRoutes;
    private LatLngBounds bounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_individual_route);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_individual_route);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_individual_route);
        navigationView.setNavigationItemSelectedListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.individual_route_map_fragment);
        mapFragment.getMapAsync(this);

        listView = (ListView) findViewById(R.id.individual_route_list);
        listOfRoutes = getIntent().getParcelableArrayListExtra(IntentConstants.CONSTANT_STRING_ROUTELIST);

        // Extract the distances from each route and add them to a list.
        ArrayList<Double> listOfDistances = createDistanceList();

        ArrayAdapter<Double> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listOfDistances);
        listView.setAdapter(listAdapter);
        setListViewHeight();
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_individual_route);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.individual_route, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit_location) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_routeplanner) {
            finish();
        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_haze) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_individual_route);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap mapLocal) {

        map = mapLocal;

        LatLng latLngStart = listOfRoutes.get(0).getPointStart();
        LatLng latLngEnd = listOfRoutes.get(0).getPointEnd();

        Marker markerStart = map.addMarker(new MarkerOptions().position(latLngStart).draggable(false));
        Marker markerEnd = map.addMarker(new MarkerOptions().position(latLngEnd).draggable(false));

        drawRoutes();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(markerStart.getPosition());
        builder.include(markerEnd.getPosition());

        for (Route route : listOfRoutes) {
            for (LatLng waypoint : route.getWaypoints()) {
                builder.include(waypoint);
            }
        }

        bounds = builder.build();

        map.setOnMapLoadedCallback(this);
        map.setOnMarkerClickListener(this);

        map.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public void onMapLoaded() {
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), TripActivity.class);
        intent.putExtra(IntentConstants.CONSTANT_STRING_ROUTE, listOfRoutes.get(position));
        startActivity(intent);
    }

    private void setListViewHeight() {
        int numberOfItems = listView.getAdapter().getCount();

        if (numberOfItems > maxNumberOfListItemsDisplayed) {
            View item = listView.getAdapter().getView(0, null, listView);
            item.measure(0, 0);
            int itemHeight = item.getMeasuredHeight();
            int dividerHeight = listView.getDividerHeight();

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = (itemHeight * maxNumberOfListItemsDisplayed) + (dividerHeight * (maxNumberOfListItemsDisplayed - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }

    private ArrayList<Double> createDistanceList() {
        double distanceInKMs;
        ArrayList<Double> listOfDistances;

        listOfDistances = new ArrayList<>();

        for (Route route : listOfRoutes) {
            distanceInKMs = route.getDistanceInMeters() / 1000;
            listOfDistances.add(distanceInKMs);
        }

        return listOfDistances;
    }

    private void drawRoutes() {
        int color;
        Random random = new Random();
        MapManager mapManager = new MapManager();

        for (Route route : listOfRoutes) {
            color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
            mapManager.drawRoute(map, route, color);
        }
    }
}
