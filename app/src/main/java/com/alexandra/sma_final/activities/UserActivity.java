package com.alexandra.sma_final.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.adapters.UserTabAdapter;
import com.alexandra.sma_final.customviews.MontserratTextView;

import com.alexandra.sma_final.rest.UserDTO;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.tabs.TabLayout;

import java.util.Random;

import androidx.viewpager.widget.ViewPager;
import io.realm.Realm;
import realm.User;

public class UserActivity extends BaseActivity {

    protected String activityName = "My profile";

    private ImageView mImage;
    private MontserratTextView mTextView;
    private ViewPager mViewPager;

    private UserDTO mCurrentUser;
    private User mUserProfile;

    final Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        String username = getIntent().getExtras().getString("username");

        UserDTO currentAppUser = ((MyApplication) getApplication()).getCurrentUser();

        //set current user
        // if no user -> no internet connection
        if(username.equals("No user")){
            mUserProfile = null;
            if(currentAppUser == null){
                mCurrentUser = null;
            }else{
                mCurrentUser = currentAppUser;
            }
        }
        // if the username received is our own -> our profile
        else if(username.equals(currentAppUser.getUsername())){
            mCurrentUser = currentAppUser;
            mUserProfile = null;
        }
        //else if the username received is NOT our own -> someone elses profile
        else if(!(username.equals(currentAppUser.getUsername()))){
            ((MyApplication)getApplication()).requestGateway.getUserByUsername(username);
            try(Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransaction(inRealm -> {
                    final User user = realm.where(User.class).equalTo("username", username).findFirst();
                    if (user != null) {
                        setUserProfileOwner(user);
                    }
                });
            }
            if(currentAppUser != null){
                mCurrentUser = currentAppUser;
            }
        }

        setListeners();
        mActivity.setText(getActivityName());
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        mImage = (ImageView) findViewById(R.id.imageView);
        final String img = "user_" + rnd.nextInt(10);
        this.mImage.setImageDrawable(
                getResources().getDrawable(getResourceID(img, "drawable",
                        getApplicationContext()))
        );

        // set MontserratTextView to user name
        mTextView = (MontserratTextView) findViewById(R.id.usernameUser);
        if(mUserProfile != null){
            mTextView.setText(mUserProfile.getUsername());
        }
        else if(mCurrentUser != null){
            mTextView.setText(mCurrentUser.getUsername());
        }
        else{
            mTextView.setText("No user");
        }

        mViewPager = (ViewPager) findViewById(R.id.tabs_view);
        UserTabAdapter myPagerAdapter = new UserTabAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(myPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mViewPager);
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
    public String getActivityName() {
        return activityName;
    }

    public User getUserProfileOwner(){
        return mUserProfile;
    }

    private void setUserProfileOwner(User user){
        mUserProfile = user;
    }


    public UserDTO getmCurrentUser(){
        return mCurrentUser;
    }

}
