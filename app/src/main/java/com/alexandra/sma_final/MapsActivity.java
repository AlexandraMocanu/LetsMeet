package com.alexandra.sma_final;

import android.Manifest;
import android.annotation.SuppressLint;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import realm.Topic;

import static android.app.PendingIntent.getActivity;
import static com.alexandra.sma_final.MainActivity.REQUEST_LOCATION_PERMISSION;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    protected String activityName = "Map";

    private GoogleMap mMap;
    private ArrayList<Marker> mRequests;

    private static final String LIST_FRAGMENT_TAG = "marker_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setListeners();
        mActivity.setText(getActivityName());
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
        bottomNavigationView.getMenu().findItem(R.id.map).setChecked(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        enableMyLocation();

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            LatLng currentPosition = new LatLng(gps.getLatitude(), gps.getLongitude());
//            mMap.addMarker(new MarkerOptions().position(currentPosition).title("You are here");
            CameraPosition cameraPosition =  new CameraPosition.Builder()
                    .target(currentPosition)    // Sets the center of the map to location user
                    .zoom(13)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        setRequestMarkers();
        mMap.setOnMarkerClickListener(this);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }

    public void setRequestMarkers(){

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<Topic> topics  = realm.where(Topic.class).findAll();
                if (topics.size() != 0){
                    mRequests = new ArrayList<>();

                    int count = 0;
                    for(Topic t : topics){
                        mRequests.add(count,
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(t.getCoordX(), t.getCoordY()))
                                        .title(t.getTitle())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))));

                        mRequests.get(count).setTag(t);

                        count++;
                    }
                }
            });
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onMarkerClick(Marker marker) {
//        Intent intent = new Intent(MapsActivity.this, MarkerActivity.class);

//        intent.putExtra("topic_id", );
//        startActivity(intent);

        Bundle bundle = new Bundle();
        Topic t = (Topic) mRequests.get(mRequests.indexOf(marker)).getTag();
        bundle.putLong("topic_id", t.getId());

        Fragment f = getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
        if (f != null) {
            getSupportFragmentManager().popBackStack();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.marker_fragment_container,
                            MarkerFragment
                                    .instantiate(this, MarkerFragment.class.getName(), bundle),
                            LIST_FRAGMENT_TAG
                    ).addToBackStack(null).commit();
        }

        return true;
    }

    @Override
    public String getActivityName() {
        return activityName;
    }
}
