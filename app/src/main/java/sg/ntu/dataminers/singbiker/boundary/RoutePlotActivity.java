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
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sg.ntu.dataminers.singbiker.IntentConstants;
import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.control.IncidentManager;
import sg.ntu.dataminers.singbiker.control.MapManager;
import sg.ntu.dataminers.singbiker.control.PcnManager;
import sg.ntu.dataminers.singbiker.control.RouteManager;
import sg.ntu.dataminers.singbiker.control.SettingsManager;
import sg.ntu.dataminers.singbiker.entity.Incident;
import sg.ntu.dataminers.singbiker.entity.PcnPoint;
import sg.ntu.dataminers.singbiker.entity.Route;

public class RoutePlotActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private PlaceAutocompleteFragment startSearchBar;
    private PlaceAutocompleteFragment endSearchBar;
    private ImageButton plotButton;
    private LatLng start=new LatLng(1.4382776,103.7809787);
    private LatLng end=new LatLng(1.4030314,103.7328157);
    private PcnManager pcnm;
    private Marker cs;
    private Marker ce;
    private Marker sexit;
    private Marker eexit;
    private PcnPoint startpp;
    private PcnPoint endpp;
//    private LatLng start;
//    private LatLng end;
    private Marker startMarker;
    private Marker endMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SettingsManager.loadSettings(this);

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
        pcnm=new PcnManager(this);
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
        MapManager.drawPcnRoutes(mMap,RoutePlotActivity.this);
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
        plotButton=(ImageButton)findViewById(R.id.plotButton);
        plotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start==null || end==null){
                    Toast.makeText(RoutePlotActivity.this,"Enter start and end points",Toast.LENGTH_LONG).show();
                    return;
                }

                updateStartPoint();
                updateEndPoint();
                Log.d("bikertag", ""+start.toString());
                Log.d("bikertag", ""+end.toString());
                Intent intent=new Intent(RoutePlotActivity.this,IndividualRouteActivity.class);
                RouteManager rm=new RouteManager(start,end,RoutePlotActivity.this);
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
//                Log.d("bikertag","list size :"+list.size());
//                for(int i=0;i<list.size();i++){
//                    Random r=new Random();
//                    Route route=list.get(i);
//                    Log.d("bikertag","drawing route");
//                    MapManager.drawRoute(mMap,route,r.nextInt());
//                }

            }
        });
    }

    private void updateStartPoint(){
        if(startMarker!=null)
            startMarker.remove();
        if(cs!=null)
            cs.remove();
        if(sexit!=null)
            sexit.remove();
        if(eexit!=null)
            eexit.remove();
        MarkerOptions mo=new MarkerOptions();
        mo.position(start);
        mo.title("Start point");
        mo.draggable(true);
        startMarker=mMap.addMarker(mo);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(start);

        startpp=pcnm.getNearestPcnPoint(start);
        MarkerOptions me=new MarkerOptions();
        me.position(new LatLng(startpp.ll.getLatitude(),startpp.ll.getLongitude()));
        me.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        cs=mMap.addMarker(me);

        if(endpp!=null){
            boolean connected=pcnm.isConnected(startpp.id,endpp.id);
            Log.d("bikertag",endpp.id+" and "+startpp.id+" connected=="+connected);
            String z=startpp.id+"";
            if(connected){
                ArrayList<Integer> path=pcnm.getPath(startpp.id,endpp.id);
                for(int x:path){
                    z=z.concat("-->"+x);
                }
                z=z.concat("-->"+endpp.id);
                Log.d("bikertag",endpp.id+" and "+startpp.id+" path=="+z);
                Toast.makeText(getBaseContext(),"Connected",Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(),"path=="+z,Toast.LENGTH_LONG).show();
            }
            else{
                LatLng[] arr=pcnm.getExitPoints(startpp,endpp);
                Log.d("bikertag",arr.length+"");
                MarkerOptions mz=new MarkerOptions();
                mz.position(arr[0]);
                mz.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                sexit=mMap.addMarker(mz);
                Log.d("bikertag","startloop marker at "+mz.getPosition());
                mz=new MarkerOptions();
                mz.position(arr[1]);
                mz.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                eexit=mMap.addMarker(mz);
                Log.d("bikertag","endloop marker at "+mz.getPosition());
                Toast.makeText(getBaseContext(),"Not Connected",Toast.LENGTH_SHORT).show();
            }
        }
        if(end!=null){
            builder.include(start);
            builder.include(end);
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }


    }
    private void updateEndPoint(){
        if(endMarker!=null)
            endMarker.remove();
        if(ce!=null)
            ce.remove();
        if(sexit!=null)
            sexit.remove();
        if(eexit!=null)
            eexit.remove();
        MarkerOptions mo=new MarkerOptions();
        mo.position(end);
        mo.title("End point");
        mo.draggable(true);
        endMarker=mMap.addMarker(mo);

        endpp=pcnm.getNearestPcnPoint(end);
        MarkerOptions me=new MarkerOptions();
        me.position(new LatLng(endpp.ll.getLatitude(),endpp.ll.getLongitude()));
        me.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        ce=mMap.addMarker(me);




        if(startpp!=null){
            boolean connected=pcnm.isConnected(startpp.id,endpp.id);
            Log.d("bikertag",endpp.id+" and "+startpp.id+" connected=="+connected);
            String z=startpp.id+"";
            if(connected){
                ArrayList<Integer> path=pcnm.getPath(startpp.id,endpp.id);
                for(int x:path){
                    z=z.concat("-->"+x);
                }
                z=z.concat("-->"+endpp.id);
                Log.d("bikertag",endpp.id+" and "+startpp.id+" path=="+z);
                Toast.makeText(getBaseContext(),"Connected",Toast.LENGTH_SHORT).show();
            }
            else{
                LatLng[] arr=pcnm.getExitPoints(startpp,endpp);
                Log.d("bikertag",arr.length+"");
                MarkerOptions mz=new MarkerOptions();
                mz.position(arr[0]);
                mz.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                sexit=mMap.addMarker(mz);
                Log.d("bikertag","startloop marker at "+mz.getPosition());
                mz=new MarkerOptions();
                mz.position(arr[1]);
                mz.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                eexit=mMap.addMarker(mz);
                Log.d("bikertag","endloop marker at "+mz.getPosition());
                Toast.makeText(getBaseContext(),"Not Connected",Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(),"path=="+z,Toast.LENGTH_LONG).show();
            }

        }
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
