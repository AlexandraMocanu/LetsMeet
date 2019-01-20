package com.alexandra.sma_final.activities;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.view.Menu;
import android.view.MenuItem;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.services.GPSTracker;
import com.alexandra.sma_final.fragments.MarkerFragment;
import com.alexandra.sma_final.R;
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
import static com.alexandra.sma_final.activities.MainActivity.REQUEST_LOCATION_PERMISSION;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    protected String activityName = "Map";

    private GoogleMap mMap;
    private ArrayList<Marker> mRequests;

    private static final String LIST_FRAGMENT_TAG = "marker_fragment";

    private Long topicId;
    private Topic mTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        try {
            topicId = getIntent().getExtras().getLong("topicId");
        } catch (Exception e) {

        }

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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (enableMyLocation() == true){
            mMap.setMyLocationEnabled(true);
        }

        GPSTracker gps = new GPSTracker(this);
        LatLng position = null;
        if (topicId != null) {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransaction(inRealm -> {
                    final Topic topic = realm.where(Topic.class).equalTo("id", topicId).findFirst();
                    if (topic != null) {
                        setTopic(topic);
                    }
                });
            }

            position = new LatLng(mTopic.getCoordX(), mTopic.getCoordY());
        } else {
            if (gps.canGetLocation()) {
                position = new LatLng(gps.getLatitude(), gps.getLongitude());
            }
        }

        if (position != null) {
            //            mMap.addMarker(new MarkerOptions().position(currentPosition).title("You are here");
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position)    // Sets the center of the map to location user
                    .zoom(13)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
        }

        setRequestMarkers();
        mMap.setOnMarkerClickListener(this);
    }

    @SuppressLint("MissingPermission")
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
                    boolean enabled = enableMyLocation();
                    if (enabled == true) {
                        mMap.setMyLocationEnabled(enabled);
                    }
                    break;
                }
        }
    }

    public void setRequestMarkers() {

        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<Topic> topics = realm.where(Topic.class).findAll();
                if (topics.size() != 0) {
                    mRequests = new ArrayList<>();

                    int count = 0;
                    for (Topic t : topics) {

                        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.final_app_icon_without_text_resized);
                        Bitmap b = bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 48, 48, false);

                        mRequests.add(count,
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(t.getCoordX(), t.getCoordY()))
                                        .title(t.getTitle())
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                ));

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

    public void setTopic(Topic t){
        this.mTopic = t;
    }
}
