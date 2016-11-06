package sg.ntu.dataminers.singbiker.boundary;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sg.ntu.dataminers.singbiker.IntentConstants;
import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.control.HistoryAdapter;
import sg.ntu.dataminers.singbiker.control.HistoryDAO;
import sg.ntu.dataminers.singbiker.entity.History;
import sg.ntu.dataminers.singbiker.entity.Route;
import sg.ntu.dataminers.singbiker.entity.Trip;

public class HistoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemClickListener {

    private boolean deletionMode = false;
    private HistoryAdapter adapter;
    private HistoryDAO historyDAO;
    private List<History> liste;

    private Menu menu;
    private Button bt1;
    private AlertDialog deleteDialog;
    private ListView vue;
    private TextView no_trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_history);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_history);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_history);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_history);

        createDeleteDialog();

        historyDAO = new HistoryDAO(this);

        /*historyDAO.open();

        //historyDAO.DANGEROUS_REMOVE_THIS_METHOD();

        historyDAO.addHistory(new History(new Trip(new Route(new LatLng(0, 0), new LatLng(1, 1))),  new Date(Long.decode("12342352353252"))));
        historyDAO.addHistory(new History(new Trip(new Route(new LatLng(13, 3), new LatLng(12, 1))),  new Date(Long.decode("8342352353252"))));
        historyDAO.addHistory(new History(new Trip(new Route(new LatLng(1434, 0), new LatLng(1, 23213))),  new Date(Long.decode("242352353252"))));
        historyDAO.addHistory(new History(new Trip(new Route(new LatLng(1212, 3333), new LatLng(3, 3))),  new Date(Long.decode("42352353252"))));

        historyDAO.close();*/

        historyDAO.open();
        getHistoryList();
        historyDAO.close();

    }

    private void getHistoryList() {

        liste = historyDAO.getAllHistory();

        vue = (ListView) findViewById(R.id.listViewHistory);
        no_trips = (TextView) findViewById(R.id.history_textview_no_trips);


        if (liste.isEmpty()) {
            vue.setVisibility(View.GONE);
            no_trips.setVisibility(View.VISIBLE);
        } else {
            vue.setVisibility(View.VISIBLE);
            no_trips.setVisibility(View.GONE);

            adapter = new HistoryAdapter(this, liste);

            vue.setAdapter(adapter);
            vue.setOnItemClickListener(this);
        }
    }


    @Override
    public void onBackPressed() {
        if (deletionMode) {
            unsetDeletionMode();
        } else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_history);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.enter_delete) {
            if (!liste.isEmpty()) {
                setDeletionMode();
            }
        } else if(id == R.id.return_delete){
            unsetDeletionMode();
        } else if (id == R.id.select_all) {
            adapter.selectAllItems();
        } else if(id == R.id.action_delete) {
            deleteDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_routeplanner) {
            Intent i = new Intent(getApplicationContext(), RoutePlotActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_haze) {

        } else if (id == R.id.nav_settings) {
            intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_history);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setDeletionMode() {

        deletionMode = true;

        menu.findItem(R.id.enter_delete).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(true);
        menu.findItem(R.id.select_all).setVisible(true);
        menu.findItem(R.id.return_delete).setVisible(true);

        adapter.setDeletionMode();
    }

    private void unsetDeletionMode() {
        deletionMode = false;

        menu.findItem(R.id.enter_delete).setVisible(true);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.select_all).setVisible(false);
        menu.findItem(R.id.return_delete).setVisible(false);

        adapter.unsetDeletionMode();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getId() == R.id.listViewHistory) {
            if (deletionMode) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox_listview_history);
                cb.setClickable(true);
                cb.performClick();
                cb.setClickable(false);
            } else {
                Intent intentWithTrip = new Intent(this, IndividualTripActivity.class);
                intentWithTrip.putExtra(IntentConstants.CONSTANT_STRING_CALLINGACTIVITY, IntentConstants.CONSTANT_INT_HISTORYLISTACTIVITY);
                intentWithTrip.putExtra(IntentConstants.CONSTANT_STRING_TRIP, ((History)adapter.getItem(position)).getTrip());
                startActivity(intentWithTrip);
            }
        }
    }

    private void deleteSelectedHistory() {
        boolean[] checked = adapter.getItemChecked();

        for (int i=0; i<checked.length; i++) {
            if(checked[i]) {
                historyDAO.removeHistory(((History)adapter.getItem(i)).getDBId());
            }
        }
    }

    private void createDeleteDialog(){
        deleteDialog = new AlertDialog.Builder(this).create();
        deleteDialog.setTitle(R.string.history_delete_dialog_name);
        deleteDialog.setMessage(getResources().getString(R.string.history_delete_dialog_message));
        deleteDialog.setCancelable(true);
        deleteDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.history_delete_dialog_confirm), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                historyDAO.open();
                deleteSelectedHistory();
                getHistoryList();
                historyDAO.close();
                dialog.dismiss();
            }
        });
        deleteDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.history_delete_dialog_cancel), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }


}
