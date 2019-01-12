package com.alexandra.sma_final;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.Pin;
import realm.Topic;
import realm.User;

public class UserActivity extends BaseActivity {

    protected String activityName = "My profile";

    private ImageView mImage;
    private TextView mTextView;
    private TextView mTextView2;
    private TextView mKarma;
    private RecyclerView mChats;
    private LinearLayoutManager layoutManager;

    private User mUser;

    final Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Long user_id = getIntent().getExtras().getLong("username_id");

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final User user  = realm.where(User.class).equalTo("ID", user_id).findFirst();
                if(user != null){
                    mUser = user;
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

        // set textView to user name
        mTextView2 = (TextView) findViewById(R.id.textView2);
        mTextView2.setText(((MyApplication)getApplication()).currentUser.getUsername());

        // set karma
        mKarma = (TextView) findViewById(R.id.karma);
        String karma = "Karma: ";
        karma = karma + mUser.getKarma();
        karma = karma + "upvotes";
        mKarma.setText(karma);

        //set recyvler view
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
}
