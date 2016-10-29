package sg.ntu.dataminers.singbiker.boundary;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.kml.KmlLayer;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import sg.ntu.dataminers.singbiker.IntentConstants;
import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.control.IncidentManager;
import sg.ntu.dataminers.singbiker.control.MapManager;
import sg.ntu.dataminers.singbiker.control.RouteManager;
import sg.ntu.dataminers.singbiker.entity.Incident;
import sg.ntu.dataminers.singbiker.entity.Point;
import sg.ntu.dataminers.singbiker.entity.Route;
import sg.ntu.dataminers.singbiker.entity.Trip;

public class RoutePlotActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private PlaceAutocompleteFragment startSearchBar;
    private PlaceAutocompleteFragment endSearchBar;
    private Button plotButton;
    private LatLng start=new LatLng(1.4382776,103.7809787);
    private LatLng end=new LatLng(1.4030314,103.7328157);
//    private LatLng start;
//    private LatLng end;
    private Marker startMarker;
    private Marker endMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_route_plot);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_route_plot);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_route_plot);
        navigationView.setNavigationItemSelectedListener(this);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Set the "Plan route"-item as pre-selected.
        navigationView.setCheckedItem(R.id.nav_routeplanner);
        initUI();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_route_plot);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
    }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.route_plot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            // The following is temporary!
            LatLng testStart = new LatLng(1.351400, 103.685803);
            LatLng testEnd = new LatLng(1.338785, 103.705700);
            LatLng testWaypoint1 = new LatLng(1.362381, 103.705002);
            LatLng testWaypoint2 = new LatLng(1.323039, 103.679787);

            List<LatLng> testList = new ArrayList<>();
            testList.add(testWaypoint1);
            testList.add(testWaypoint2);

            Route testRoute = new Route(testStart, testEnd);
            testRoute.setDistanceInMeters(10000);
            testRoute.setWaypoints(testList);

            Trip testTrip = new Trip(testRoute);
            testTrip.setDateFinished(new Date(System.currentTimeMillis() + 3600000 * 3));
            testTrip.setRouteCycled(testRoute);
            testTrip.calculateAverageSpeed();

            Intent i = new Intent(getApplicationContext(), IndividualTripActivity.class);
            i.putExtra(IntentConstants.CONSTANT_STRING_CALLINGACTIVITY, IntentConstants.CONSTANT_INT_ROUTEPLOTACTIVITY);
            i.putExtra(IntentConstants.CONSTANT_STRING_TRIP, testTrip);
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_routeplanner) {

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
            mMap=googleMap;

        LatLng singapore = new LatLng(1.3380694,103.9052101);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore,11));
        MapManager mm=new MapManager();
        mm.drawPcnRoutes(mMap,RoutePlotActivity.this);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng pos=marker.getPosition();
                String loc=getLocation(pos);
                if(marker.getTitle().equals("Start point")){
                    start=marker.getPosition();
                    startSearchBar.setText(loc);
                    updateStartPoint();
                }
                else if(marker.getTitle().equals("End point")){
                    end=marker.getPosition();
                    endSearchBar.setText(loc);
                    updateEndPoint();
                }
            }
        });
        GetData gd=new GetData();
        gd.execute();
    }
    private void initUI(){
        AutocompleteFilter filter=new AutocompleteFilter.Builder().setCountry("SG").build();
        startSearchBar=(PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.startSearchBar);
        startSearchBar.setHint("Enter start point");
        startSearchBar.getView().setBackgroundColor(Color.WHITE);
        startSearchBar.setOnPlaceSelectedListener(new PlaceSelectionListener(){
            public void onPlaceSelected(Place p){
                Log.d("bikertag",p.getName()+" ");
                start=p.getLatLng();
                updateStartPoint();
            }
            public void onError(Status s){

            }
        });
        startSearchBar.setFilter(filter);
        endSearchBar=(PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.endSearchBar);
        endSearchBar.setHint("Enter end point");
        endSearchBar.getView().setBackgroundColor(Color.WHITE);
        endSearchBar.setOnPlaceSelectedListener(new PlaceSelectionListener(){
            public void onPlaceSelected(Place p){
                end=p.getLatLng();
                updateEndPoint();
            }
            public void onError(Status s){

            }
        });
        endSearchBar.setFilter(filter);
        plotButton=(Button)findViewById(R.id.plotButton);
        plotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start==null || end==null){
                    Toast.makeText(RoutePlotActivity.this,"Enter start and end points",Toast.LENGTH_LONG);
                    return;
                }
                updateStartPoint();
                updateEndPoint();
                Log.d("bikertag", ""+start.toString());
                Log.d("bikertag", ""+end.toString());
                Intent intent=new Intent(RoutePlotActivity.this,IndividualRouteActivity.class);
                RouteManager rm=new RouteManager(start,end);
                rm.execute();
                ArrayList<Route> list=null;
                while(!rm.isDone()){
                    try {
                        Thread.sleep(100);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                list=rm.getRoutes();//this is the list that needs to be passed to the next activity
                Log.d("bikertag","The size of list sent == "+list.size());
                intent.putParcelableArrayListExtra(IntentConstants.CONSTANT_STRING_ROUTELIST, list);
                startActivity(intent);
//                MapManager mm=new MapManager();
//                Log.d("bikertag","list size :"+list.size());
//                for(int i=0;i<list.size();i++){
//                    Random r=new Random();
//                    Route route=list.get(i);
//                    Log.d("bikertag","drawing route");
//                    mm.drawRoute(mMap,route,r.nextInt());
//                }

            }
        });
    }

    private void updateStartPoint(){
        if(startMarker!=null)
            startMarker.remove();
        MarkerOptions mo=new MarkerOptions();
        mo.position(start);
        mo.title("Start point");
        mo.draggable(true);
        startMarker=mMap.addMarker(mo);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(start);
        if(end!=null){
            builder.include(end);
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }


    }
    private void updateEndPoint(){
        if(endMarker!=null)
            endMarker.remove();
        MarkerOptions mo=new MarkerOptions();
        mo.position(end);
        mo.title("End point");
        mo.draggable(true);
        endMarker=mMap.addMarker(mo);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if(start!=null){
            builder.include(start);
            builder.include(end);
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }


    }
    class GetData extends AsyncTask<Void,Void,ArrayList<Incident>> {
        protected ArrayList<Incident> doInBackground(Void... params){
            IncidentManager im=new IncidentManager();
            return im.getIncidents();
        }

        @Override
        protected void onPostExecute(ArrayList<Incident> list) {
            MarkerOptions mo=new MarkerOptions();
            for (Incident i:list){
                mo.position(new LatLng(i.getLocation().getLatitude(),i.getLocation().getLongitude()));
                mo.title(i.getType());
                mo.snippet(i.getDescription());
                mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.incident_icon));
                mMap.addMarker(mo);
            }
        }
    }

    public String getLocation(LatLng ll){
        String loc="";
        List<Address> list;
        Geocoder g=new Geocoder(RoutePlotActivity.this, Locale.getDefault());
        try{
            list=g.getFromLocation(ll.latitude,ll.longitude,1);
            loc=list.get(0).getAddressLine(0);
        }catch(Exception e){
            Log.d("bikertag",e.toString());
        }

        return loc;
    }
}
