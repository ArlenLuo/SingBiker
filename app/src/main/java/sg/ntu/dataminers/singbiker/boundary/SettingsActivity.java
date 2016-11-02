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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.control.SettingsManager;
import sg.ntu.dataminers.singbiker.entity.Settings;
import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RadioButton radioButtonMetric;
    private RadioButton radioButtonImperial;
    private Button buttonPCNColor;
    private Button buttonNonPCNColor;
    private boolean unitSystemMetric;
    private int colorPCN;
    private int colorNonPCN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_settings);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_settings);

        unitSystemMetric = Settings.isUnitSystemMetric();
        colorPCN = Settings.getColorPCN();
        colorNonPCN = Settings.getColorNonPCN();

        findReferencesToUIComponents();
        fillUIComponentsWithSettings();
        setButtonsOnClickListeners();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save_settings) {

            if (radioButtonMetric.isChecked()) {
                Settings.setUnitSystem(true);
            }
            else {
                Settings.setUnitSystem(false);
            }

            Settings.setColorPCN(colorPCN);
            Settings.setColorNonPCN(colorNonPCN);

            SettingsManager.saveSettings(this);

            Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.action_discard_settings) {
            finish();
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
            intent = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_haze) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void findReferencesToUIComponents() {
        radioButtonMetric = (RadioButton) findViewById(R.id.settings_radiobutton_metric);
        radioButtonImperial = (RadioButton) findViewById(R.id.settings_radiobutton_imperial);
        buttonPCNColor = (Button) findViewById(R.id.settings_button_pcn_color);
        buttonNonPCNColor = (Button) findViewById(R.id.settings_button_non_pcn_color);
    }

    private void fillUIComponentsWithSettings() {

        if (unitSystemMetric) {
            radioButtonMetric.setChecked(true);
        }
        else {
            radioButtonImperial.setChecked(true);
        }

        buttonPCNColor.setBackgroundColor(colorPCN);
        buttonNonPCNColor.setBackgroundColor(colorNonPCN);
    }

    private void setButtonsOnClickListeners() {

        buttonPCNColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AmbilWarnaDialog(v.getContext(), colorPCN, new AmbilWarnaDialog.OnAmbilWarnaListener() {

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        colorPCN = color;
                        buttonPCNColor.setBackgroundColor(colorPCN);
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {}

                }).show();
            }
        });

        buttonNonPCNColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AmbilWarnaDialog(v.getContext(), colorNonPCN, new AmbilWarnaDialog.OnAmbilWarnaListener() {

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        colorNonPCN = color;
                        buttonNonPCNColor.setBackgroundColor(colorNonPCN);
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {}

                }).show();
            }
        });
    }
}
