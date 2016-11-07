package sg.ntu.dataminers.singbiker.boundary;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.control.HazeManager;
import sg.ntu.dataminers.singbiker.entity.Haze;


public class HazeActivity extends AppCompatActivity
        implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener{

    private GoogleMap mMap;
    private ArrayList<Haze> hazeList;
    private LatLng North, South, East, West, Central;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haze);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_haze);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_haze);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_haze);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_haze);

        //map gives null pointer error
        setContentView(R.layout.content_haze);
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                //.findFragmentById(R.id.map);
        //MapFragment mapFragment = (MapFragment) getFragmentManager() .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        HazeManager hm = new HazeManager();
        ArrayList<Haze> hazeList = hm.getHazeInfo();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private Float psi(double PSIvalue) {
        if (PSIvalue > 300) {
            return BitmapDescriptorFactory.HUE_RED;} //0.0
        else if (PSIvalue > 200) {
         return BitmapDescriptorFactory.HUE_ORANGE;} //30.0
        else if (PSIvalue > 100) {
          return BitmapDescriptorFactory.HUE_YELLOW;} //60.0
        else if (PSIvalue >50) {
          return BitmapDescriptorFactory.HUE_AZURE;} //210.0
        else  {
          return BitmapDescriptorFactory.HUE_GREEN;} //120.0
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

        LatLng singapore = new LatLng(1.3380694,103.9052101);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore,11));

        North = new LatLng(1.41803,103.82);
        mMap.addMarker(new MarkerOptions().position(North)
                .title("North")
                .snippet(Double.toString(hazeList.get(0).getPsiLevel()))
                .icon(BitmapDescriptorFactory.defaultMarker(psi(hazeList.get(0).getPsiLevel()))));

        South = new LatLng(1.29587,103.82);
        mMap.addMarker(new MarkerOptions().position(South)
                .title("South")
                .snippet(Double.toString(hazeList.get(1).getPsiLevel()))
                .icon(BitmapDescriptorFactory.defaultMarker(psi(hazeList.get(1).getPsiLevel()))));

        East =  new LatLng(1.35735,103.94);
        mMap.addMarker(new MarkerOptions().position(East)
                .title("East")
                .snippet(Double.toString(hazeList.get(2).getPsiLevel()))
                .icon(BitmapDescriptorFactory.defaultMarker(psi(hazeList.get(2).getPsiLevel()))));

        West = new LatLng(1.35735,103.7);
        mMap.addMarker(new MarkerOptions().position(West)
                .title("West")
                .snippet(Double.toString(hazeList.get(3).getPsiLevel()))
                .icon(BitmapDescriptorFactory.defaultMarker(psi(hazeList.get(3).getPsiLevel()))));

        Central = new LatLng(1.35735,103.82);
        mMap.addMarker(new MarkerOptions().position(Central)
                .title("Central")
                .snippet(Double.toString(hazeList.get(4).getPsiLevel()))
                .icon(BitmapDescriptorFactory.defaultMarker(psi(hazeList.get(4).getPsiLevel()))));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_routeplanner) {
            Intent i = new Intent(getApplicationContext(), RoutePlotActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_history) {
            Intent i = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_haze) {

        } else if (id == R.id.nav_settings) {
            intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_route_plot);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
