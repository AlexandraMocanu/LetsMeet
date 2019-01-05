package com.alexandra.sma_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends BaseActivity {

    protected String activityName = "Home";

    public static final int REQUEST_LOCATION_PERMISSION = 99;
//    protected FloatingActionButton fab;

    private ScrollView mScrollView;
    private RecyclerView mChats;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setListeners();
        mActivity.setText(getActivityName());
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);

        mScrollView = (ScrollView) findViewById(R.id.scroll_view);

        mChats = (RecyclerView) findViewById(R.id.chats);
        mChats.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(this);
        mChats.setLayoutManager(layoutManager);
        mChats.setItemAnimator(new DefaultItemAnimator());

        //get chats of user
        //create chat adapter and set it to layout manager
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
