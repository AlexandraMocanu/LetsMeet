package com.alexandra.sma_final;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.alexandra.sma_final.view.MontserratEditText;

import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class PostRequestActivity extends BaseActivity implements PlaceSelectionListener {

    protected String activityName = "New request";

    private TextInputEditText mTitle;
    private TextInputEditText mMessage;
    private MontserratEditText mDate;
    private MontserratEditText mTime;
//    private AutoCompleteTextView mAddress;
    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private Place selectedAddress;
    private ImageButton mPostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postrequest);

        setListeners();
        mActivity.setText(getActivityName());
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        mTitle = (TextInputEditText) findViewById(R.id.title);
        mMessage = (TextInputEditText) findViewById(R.id.message);
        mDate = (MontserratEditText) findViewById(R.id.date);
        mTime = (MontserratEditText) findViewById(R.id.time);
//        mAddress = (AutoCompleteTextView) findViewById(R.id.address);
        mPostButton = (ImageButton) findViewById(R.id.buttonPost);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setHint("Enter place of event here");

        autocompleteFragment.setOnPlaceSelectedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlaceSelected(Place place) {
        this.selectedAddress = place;
        Toast.makeText(getApplicationContext(), "" + place.getName() + place.getLatLng(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(getApplicationContext(), "" + status.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public String getActivityName() {
        return activityName;
    }
}
