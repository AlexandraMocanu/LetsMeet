package com.alexandra.sma_final;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexandra.sma_final.rest.UserDTO;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Random;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.City;
import realm.Topic;
import realm.User;

public class UserActivity extends BaseActivity {

    protected String activityName = "My profile";

    private ImageView mImage;
    private TextView mTextView;
    private ViewPager mViewPager;

    private UserDTO mCurrentUser;
    private User mUser;

    final Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        String username = getIntent().getExtras().getString("username_id");
        //TODO: change to this when using the server
//        AsyncResponse<UserDTO> response = new AsyncResponse<UserDTO>() {
//            @Override
//            public void processFinish(UserDTO output) {
//                mUser = output;
//                if(output.getID() != user_id){
//                    activityName = output.getUsername();
//                }
//            }
//        };
//        ((MyApplication) getApplication()).requestGateway.getCurrentUser(response);

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
            mCurrentUser = ((MyApplication) getApplication()).getCurrentUser();
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

        // set textView to user name
        mTextView = (TextView) findViewById(R.id.textView);
        if(mUser != null){
            mTextView.setText(mUser.getUsername());
        }
        if(mCurrentUser != null){
            mTextView.setText(mCurrentUser.getUsername());
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
