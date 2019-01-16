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

import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.ArrayList;
import java.util.Random;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.City;
import realm.Topic;
import realm.User;

public class UserActivity extends BaseActivity {

    protected String activityName = "My profile";

    private RecyclerView mActiveRequests;
    private ActiveRequestsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Topic> activeRequests;

    private ImageView mImage;
    private TextView mTextView;
    private TextView mTextView2;
    private TextView mKarma;
    private ImageButton mUpButton;
    private ImageButton mDownButton;
    private TextView mVote;

    private User mUser;

    final Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Long user_id = getIntent().getExtras().getLong("username_id");

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final User user  = realm.where(User.class).equalTo("id", user_id).findFirst();
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
        mTextView = (TextView) findViewById(R.id.textView);
//        mTextView2.setText(((MyApplication)getApplication()).getCurrentUser().getUsername());
        mTextView.setText(mUser.getUsername());

        // set karma
        mKarma = (TextView) findViewById(R.id.karma);
        String karma = "Karma: ";
        karma = karma + mUser.getKarma();
        karma = karma + " karma";
        mKarma.setText(karma);

        mUpButton = (ImageButton) findViewById(R.id.upButton);
        mDownButton = (ImageButton) findViewById(R.id.downButton);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            private int clicked = 0;

            @Override
            public void onClick(View view) {
                // set rateing + 1
                if(clicked == 0){
                    mVote.setText("+1");
                    clicked = 1;
                }else{
                    mVote.setText("0");
                    clicked = 0;
                }

                mUpButton.setPressed(true);
                mDownButton.setPressed(false);
            }
        });
        mDownButton.setOnClickListener(new View.OnClickListener() {
            private int clicked = 0;

            @Override
            public void onClick(View view) {
                // set rating - 1
                if(clicked == 0){
                    mVote.setText("-1");
                    clicked = 1;
                }else{
                    mVote.setText("0");
                    clicked = 0;
                }

                mUpButton.setPressed(false);
                mDownButton.setPressed(true);
            }
        });
        mVote = (TextView) findViewById(R.id.vote);
        mVote.setText("0");

        //set recyvler view
        mActiveRequests = (RecyclerView) findViewById(R.id.active_requests);
        mLayoutManager = new LinearLayoutManager(this);
        mActiveRequests.setLayoutManager(mLayoutManager);
        ArrayList<Topic> activeTopics = getActiveRequests();
        mAdapter = new ActiveRequestsAdapter(activeTopics);
        mActiveRequests.setAdapter(mAdapter);
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

    private ArrayList<Topic> getActiveRequests(){
        ArrayList<Topic> activeR = new ArrayList<>();
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<Topic> topics  = realm.where(Topic.class).findAll();
                for(Topic t : topics){
                    if(t.getPostedBy().getId() == mUser.getId()) {
                        activeR.add(t);
                    }
                }
            });
        }

        return activeR;
    }

    private class ActiveRequestsAdapter extends RecyclerView.Adapter<ActiveRequestsAdapter.MyViewHolder> {

        private ArrayList<Topic> activeRequests;

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public TextView topicTitle;
            public Button mSeeMoreButton;

            public MyViewHolder(View itemView){
                super(itemView);

                this.topicTitle = (TextView) itemView.findViewById(R.id.topic_title);
                this.mSeeMoreButton = (Button) itemView.findViewById(R.id.see_more_);
            }

        }

        public ActiveRequestsAdapter(ArrayList<Topic> results){
            activeRequests = results;
        }

        @Override
        public ActiveRequestsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View topicView = inflater.inflate(R.layout.topic_item, parent, false);

            ActiveRequestsAdapter.MyViewHolder vh = new ActiveRequestsAdapter.MyViewHolder(topicView);
            return vh;
        }

        @Override
        public void onBindViewHolder(ActiveRequestsAdapter.MyViewHolder viewHolder, int position) {
            Topic topic = activeRequests.get(position);

            TextView textView = viewHolder.topicTitle;
            textView.setText(topic.getTitle().substring(0, (topic.getTitle().length() > 15 ? 15 : topic.getTitle().length() - 2)) + "...");
            Button seeMoreButton = viewHolder.mSeeMoreButton;
            seeMoreButton.setText("See More");
            seeMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long topicID = topic.getId();

                    Intent mIntent = new Intent(v.getContext(), RequestActivity.class);
                    mIntent.putExtra("topic_id", topicID);
                    v.getContext().startActivity(mIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return activeRequests.size();
        }

        public void addTopic(Topic c){
            activeRequests.add(c);
        }

        public boolean contains(Topic c){
            for(int i = 0; i < activeRequests.size(); i++){
                if(activeRequests.get(i).getId() == c.getId()){
                    return true;
                }
            }
            return false;
        }
    }
}
