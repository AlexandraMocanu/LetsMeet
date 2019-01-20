package com.alexandra.sma_final.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.adapters.ConversationAdapter;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.ArrayList;
import java.util.Comparator;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.Conversation;
import realm.User;

public class MainActivity extends BaseActivity {

    protected String activityName = "Home";

    public static final int REQUEST_LOCATION_PERMISSION = 99;
//    protected FloatingActionButton fab;

    private static RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private ArrayList<User> mUsers;
    private ArrayList<Conversation> mConversations;
    private static ConversationAdapter adapter;

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
//        ((MyApplication)getApplicationContext()).requestGateway.getUserConversations();

        mUsers = new ArrayList<User>();
        mConversations = new ArrayList<Conversation>();
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<Conversation> convs  = realm.where(Conversation.class).findAll();
                if (convs.size() != 0){
                    for (Conversation c: convs){
                        addConversation(c);
                    }
                }
            });
        }

        // sort conversations on latest created
        sortConv();

        adapter = new ConversationAdapter(mConversations, this);

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

    public void addConversation(Conversation c){
        mConversations.add(c);
    }

    public void sortConv(){
        mConversations.sort(new Comparator<Conversation>() {
            @Override
            public int compare(Conversation o1, Conversation o2) {
                if(o1.getId() > o2.getId()){
                    return 1;
                }else if(o1.getId() < o2.getId()){
                    return -1;
                }
                return 0;
            }
        });
    }
}
