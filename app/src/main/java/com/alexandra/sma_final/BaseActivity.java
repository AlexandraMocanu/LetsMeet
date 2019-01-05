package com.alexandra.sma_final;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alexandra.sma_final.MovableFloatingActionButton.MovableFloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

abstract public class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    final Random rnd = new Random();

    protected MovableFloatingActionButton fab;
    protected BottomNavigationView bottomNavigationView;
    protected ImageButton mMyIcon;
    protected TextView mActivity;
//    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.app_fab);
//        setSupportActionBar(toolbar);

        setContentView(R.layout.activity_base);

        setListeners();

    }

    protected void setListeners(){

        fab = (MovableFloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PostRequestActivity.class));
            }
        });

        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
//        bottomNavigationView.getMenu().findItem().setChecked(true);

        mMyIcon = (ImageButton) findViewById(R.id.my_profile);
        mMyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UserActivity.class));
            }
        });
//        final String img = "user_icon_" + rnd.nextInt(10);
//        this.mMyIcon.setImageDrawable(
//                getResources().getDrawable(getResourceID(img, "drawable",
//                        getApplicationContext()))
//        );
        mMyIcon.setImageDrawable(getResources().getDrawable(getResourceID("person", "drawable",
                        getApplicationContext())));

        mActivity = (TextView) findViewById(R.id.titleAct);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean result = BaseActivity.this.onOptionsItemSelected(item);
//                        bottomNavigationView.setSelectedItemId(item.getItemId());
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        if (id == R.id.map){
            startActivity(new Intent(this, MapsActivity.class));
        }
        if (id == R.id.pinned_action){
            startActivity(new Intent(this, MyPinsActivity.class));
        }
        if (id == R.id.dashboard_action){
            startActivity(new Intent(this, DashboardActivity.class));
        }
        if (id == R.id.home){
            startActivity(new Intent(this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    abstract public String getActivityName();

    protected final static int getResourceID (final String resName, final String resType, final Context ctx)
    {
        final int ResourceID = ctx.getResources().getIdentifier(
                resName, resType, ctx.getApplicationInfo().packageName);
        if (ResourceID == 0) {
            throw new IllegalArgumentException ("No resource string found with name " + resName);
        }
        else {
            return ResourceID;
        }
    }
}
