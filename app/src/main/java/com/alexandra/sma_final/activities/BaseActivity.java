package com.alexandra.sma_final.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.customviews.MontserratTextView;

import com.alexandra.sma_final.customviews.floatingbutton.MovableFloatingActionButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.alexandra.sma_final.activities.MainActivity.REQUEST_LOCATION_PERMISSION;

abstract public class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    protected static final String TAG = "Let's Meet";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    final Random rnd = new Random();

    protected MovableFloatingActionButton fab;
    protected BottomNavigationView bottomNavigationView;
    protected ImageButton mMyIcon;
    protected ImageButton mBackButton;
    protected MontserratTextView mActivity;
//    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.app_fab);
//        setSupportActionBar(toolbar);

        displayLocationSettingsRequest(getBaseContext());

        setContentView(R.layout.activity_base);

        setListeners();

    }

    protected void setListeners(){

        fab = (MovableFloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(getApplicationContext(), PostRequestActivity.class);
                newIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);
            }
        });
        if(this instanceof PostRequestActivity){
            fab.setEnabled(false);
            fab.setAlpha(0.0F);
        }

        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_bar);

        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
//        bottomNavigationView.getMenu().findItem().setChecked(true);

        mMyIcon = (ImageButton) findViewById(R.id.my_profile);
        mMyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), UserActivity.class);
                mIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP|FLAG_ACTIVITY_NEW_TASK);

                String n =  ((MyApplication) getApplication()).getCurrentUser().getUsername();
                if(!n.contains("fallback")){
                    mIntent.putExtra("username", ((MyApplication) getApplication()).getCurrentUser().getUsername());
                }else{
                    mIntent.putExtra("username", "No user");
                }
                getApplicationContext().startActivity(mIntent);
            }
        });
        if(this instanceof PostRequestActivity){
            mMyIcon.setEnabled(false);
            mMyIcon.setAlpha(0.0F);
        }

//        final String img = "user_icon_" + rnd.nextInt(10);
//        this.mMyIcon.setImageDrawable(
//                getResources().getDrawable(getResourceID(img, "drawable",
//                        getApplicationContext()))
//        );
//        mMyIcon.setImageDrawable(getResources().getDrawable(getResourceID("person", "drawable",
//                        getApplicationContext())));

        mBackButton = (ImageButton) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mActivity = (MontserratTextView) findViewById(R.id.titleAct);

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
            if(!(this instanceof SettingsActivity)){
                Intent newIntent = new Intent(this, SettingsActivity.class);
//                newIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);
            }
        }
        if (id == R.id.map){
            if(!(this instanceof MapsActivity)){
                Intent newIntent = new Intent(this, MapsActivity.class);
//                newIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);
            }
        }
        if (id == R.id.pinned_action){
            if(!(this instanceof MyPinsActivity)){
                Intent newIntent = new Intent(this, MyPinsActivity.class);
//                newIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);
            }
        }
        if (id == R.id.dashboard_action){
            if(!(this instanceof DashboardActivity)){
                Intent newIntent = new Intent(this, DashboardActivity.class);
//                newIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);
            }
        }
        if (id == R.id.home){
            if(!(this instanceof MainActivity)){
                Intent newIntent = new Intent(this, MainActivity.class);
//                newIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    abstract public String getActivityName();

    public final static int getResourceID (final String resName, final String resType, final Context ctx)
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

    public boolean enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return true;
        }
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(BaseActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });

        ((MyApplication)getApplication()).setLocation();
    }
}
