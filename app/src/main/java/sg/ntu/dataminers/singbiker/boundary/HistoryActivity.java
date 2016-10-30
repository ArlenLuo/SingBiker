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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.control.HistoryManager;
import sg.ntu.dataminers.singbiker.entity.History;

public class HistoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private HistoryManager historyMan;
    private boolean deletionMode = false;
    private Menu menu;
    SimpleAdapter adapter_normal;
    SimpleAdapter adapter_delete;

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

        historyMan = new HistoryManager();

        //Here retrieve the history stored in the DATABASE

        historyMan.addHistory(new History(null, new Date(Long.decode("12342352353252"))));
        historyMan.addHistory(new History(null,  new Date(Long.decode("8342352353252"))));
        historyMan.addHistory(new History(null,  new Date(Long.decode("242352353252"))));
        historyMan.addHistory(new History(null,  new Date(Long.decode("42352353252"))));

        ListView vue = (ListView) findViewById(R.id.listViewHistory);
        TextView no_trips = (TextView) findViewById(R.id.history_textview_no_trips);


        if (historyMan.checkEmpty()) {
            vue.setVisibility(View.GONE);
        } else {
            no_trips.setVisibility(View.GONE);

            List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> elem;
            SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");

            for(int i=0; i < historyMan.getListSize(); i++) {
                elem = new HashMap<String, String>();

                elem.put("image", Integer.toString(R.drawable.ic_menu_bike));

                elem.put("date", dformat.format(historyMan.getElem(i).getDate().getTime()));
                elem.put("kilometers", "42");

                liste.add(elem);
            }

            String[] from = {"image", "date", "kilometers"};
            int[] to = {R.id.image_listview_history, R.id.text1_listview_history, R.id.text2_listview_history};

            adapter_normal = new SimpleAdapter(this,
                    liste,
                    R.layout.listview_history,
                    from,
                    to);

            adapter_delete = new SimpleAdapter(this,
                    liste,
                    R.layout.listview_history_deletion,
                    from,
                    to);

            vue.setAdapter(adapter_normal);
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
            Toast.makeText(getApplicationContext(), "DELETE ALL", Toast.LENGTH_SHORT).show();

            setDeletionMode();
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
        menu.findItem(R.id.action_delete_all).setVisible(true);
        menu.findItem(R.id.select_all).setVisible(true);

        ((ListView) findViewById(R.id.listViewHistory)).setAdapter(adapter_delete);

    }

    private void unsetDeletionMode() {
        deletionMode = false;

        menu.findItem(R.id.enter_delete).setVisible(true);
        menu.findItem(R.id.action_delete_all).setVisible(false);
        menu.findItem(R.id.select_all).setVisible(false);

        ((ListView) findViewById(R.id.listViewHistory)).setAdapter(adapter_normal);
    }


}
