package com.alexandra.sma_final.activities;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.customviews.MontserratEditText;

import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import realm.Message;
import realm.Topic;

public class PostRequestActivity extends BaseActivity implements PlaceSelectionListener {

    protected String activityName = "New request";

    private TextInputEditText mTitle;
    private TextInputEditText mMessage;
//    private MontserratEditText mDate;
//    private MontserratEditText mTime;
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
//        mDate = (MontserratEditText) findViewById(R.id.date);
//        mTime = (MontserratEditText) findViewById(R.id.time);
//        mAddress = (AutoCompleteTextView) findViewById(R.id.address);
        mPostButton = (ImageButton) findViewById(R.id.buttonPost);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setHint("Enter place of event here");

        autocompleteFragment.setOnPlaceSelectedListener(this);

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Topic newTopic = new Topic();
                newTopic.setTitle(mTitle.getText().toString());

                Message newMessage = new Message();
                newMessage.setText(mMessage.getText().toString());
                newMessage.setTimestampMillis(System.currentTimeMillis());
                newTopic.setMessage(newMessage);

                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(selectedAddress.getLatLng().latitude,
                            selectedAddress.getLatLng().latitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                newTopic.setCity(addresses.get(0).getLocality());
                newTopic.setArchived(false);
                newTopic.setCoordX(selectedAddress.getLatLng().latitude);
                newTopic.setCoordY(selectedAddress.getLatLng().longitude);

                ((MyApplication) getApplication()).requestGateway.putTopic(newTopic);
            }
        });

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
