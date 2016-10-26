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
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import sg.ntu.dataminers.singbiker.IntentConstants;
import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.entity.Point;
import sg.ntu.dataminers.singbiker.entity.Route;
import sg.ntu.dataminers.singbiker.entity.Trip;

public class RoutePlotActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

        // Set the "Plan route"-item as pre-selected.
        navigationView.setCheckedItem(R.id.nav_routeplanner);
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
            Point testStart = new Point(1.351400, 103.685803);
            Point testEnd = new Point(1.338785, 103.705700);

            Route testRoute = new Route(testStart, testEnd, "");
            testRoute.setDistanceInMeters(10000);

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

        if (id == R.id.nav_routeplanner) {
            // The following is temporary!
            Intent i = new Intent(getApplicationContext(), IndividualRouteActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_history) {
            Intent i = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_haze) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_route_plot);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
