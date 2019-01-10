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

public class UserActivity extends BaseActivity {

    protected String activityName = "My profile";

    private ImageView mImage;
    private TextView mTextView;
    private TextView mTextView2;
    private TextView mKarma;
    private RecyclerView mChats;
    private LinearLayoutManager layoutManager;

    final Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

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

        // set karma
        mKarma = (TextView) findViewById(R.id.karma);
        String karma = "Karma: ";
//        karma = karma + getScore();
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
