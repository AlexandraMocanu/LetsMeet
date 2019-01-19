package com.alexandra.sma_final;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.alexandra.sma_final.view.MontserratTextView;

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
    private User mUser;

    final Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        String username = getIntent().getExtras().getString("username_id");

        //TODO: uncomment this for server run!
        if(username != ((MyApplication) getApplication()).getCurrentUser().getUsername()){
            try(Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransaction(inRealm -> {
                    final User user = realm.where(User.class).equalTo("username", username).findFirst();
                    if (user != null) {
                        setCurrentUser(user);
                    }
                });
            }
        }
        else{
            if(username != "No user"){
                mCurrentUser = ((MyApplication) getApplication()).getCurrentUser();
            }
        }

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final User user = realm.where(User.class).equalTo("username", username).findFirst();
                if (user != null) {
                    setCurrentUser(user);
                }
            });
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
        if(mUser != null){
            mTextView.setText(mUser.getUsername());
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

    //TODO: change to this when using the server
    public UserDTO getCurrentUser(){
        return mCurrentUser;
    }

    public User getUser(){
        return mUser;
    }

    public void setCurrentUser(User user){
        mUser = user;
    }

//    public User getUser(){
//        return mUser;
//    }

}
