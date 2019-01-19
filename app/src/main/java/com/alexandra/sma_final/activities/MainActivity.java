package com.alexandra.sma_final.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.adapters.UserAdapter;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.ArrayList;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.User;

public class MainActivity extends BaseActivity {

    protected String activityName = "Home";

    public static final int REQUEST_LOCATION_PERMISSION = 99;
//    protected FloatingActionButton fab;

    private static RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private ArrayList<User> mUsers;
    private static UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setListeners();
        mActivity.setText(getActivityName());
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);

        recyclerView = (RecyclerView) findViewById(R.id.chats);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //get chats of user
        //create chat adapter and set it to layout manager
        ((MyApplication)getApplicationContext()).requestGateway.getUserConversations();

        mUsers = new ArrayList<User>();
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<User> users  = realm.where(User.class).findAll();
                if (users.size() != 0){
                    for (User u: users){
                        addUser(u);
                    }
                }
            });
        }

//        AsyncResponse<ArrayList<UserDTO>> response = new AsyncResponse<ArrayList<UserDTO>>() {
//            @Override
//            public void processFinish(ArrayList<UserDTO> output) {
//                for (UserDTO u : output){
//                    mUsers.add(u);
//                }
//            }
//        };
//        ((MyApplication) getApplication()).requestGateway.getAllUsers(response);

        // sort newest message

        adapter = new UserAdapter(mUsers, this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    public void addUser(User u){
        mUsers.add(u);
    }
}
