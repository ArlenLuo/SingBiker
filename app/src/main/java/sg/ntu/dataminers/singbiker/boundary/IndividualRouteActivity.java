package sg.ntu.dataminers.singbiker.boundary;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.entity.Route;

public class IndividualRouteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, AdapterView.OnItemClickListener {

    private static final int maxNumberOfListItemsDisplayed = 4;
    private GoogleMap map;
    private ListView listView;

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
        String[] testValues = new String[] {"Listitem 1", "Listitem 2", "Listitem 3", "Listitem 4", "Listitem 5", "Listitem 6", "Listitem 7", "Listitem 8"};
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, testValues);
        listView.setAdapter(listAdapter);
        setListViewHeight();
        listView.setOnItemClickListener(this);
        ArrayList<Route> list=getIntent().getParcelableArrayListExtra("rlist");
        Log.d("bikertag","The size of list received == "+list.size());
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String itemValue = (String) listView.getItemAtPosition(position);
        Toast.makeText(getApplicationContext(), "Position: " + position + "\nValue: " + itemValue, Toast.LENGTH_SHORT).show();
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
}
