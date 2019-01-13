package com.alexandra.sma_final;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmQuery;
import realm.Pin;
import realm.Topic;

public class RequestActivity extends BaseActivity {

    protected String activityName = "Request Details";

    private Button mButtonMessage;
    private Button mButtonSeeOnMap;
    private Button mButtonPin;
    private ImageView mRandomPicture;
    private TextView mTextTitle;
    private TextView mTextMessage;
    private TextView mTextAuthor;
    private Long topicID;
    private Topic mTopic;

    final Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_details);

        setListeners();
        mActivity.setText(getActivityName());
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        this.mButtonMessage = (Button) findViewById(R.id.message_author);
        this.mButtonSeeOnMap = (Button) findViewById(R.id.see_map);
        this.mButtonPin = (Button) findViewById(R.id.pin_button);
        this.mRandomPicture = (ImageView) findViewById(R.id.random_picture);
        this.mTextTitle = (TextView) findViewById(R.id.title);
        this.mTextMessage = (TextView) findViewById(R.id.title);
        this.mTextAuthor = (TextView) findViewById(R.id.author);

        topicID = getIntent().getExtras().getLong("topic_id");

        final String img = "img_" + rnd.nextInt(11);
        this.mRandomPicture.setImageDrawable(
                getResources().getDrawable(getResourceID(img, "drawable",
                        getApplicationContext()))
                );

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmQuery<Topic> topic  = realm.where(Topic.class).equalTo("ID",topicID);
                if (topic.count() > 0){
                    mTopic = topic.findFirst();
                    if (mTopic == null){

                    }
                }
            });
        }

        mTextAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), RequestActivity.class);
                mIntent.putExtra("username_id", mTopic.getPostedBy().getID());
                getApplicationContext().startActivity(mIntent);
            }
        });

        SpannableStringBuilder str = new SpannableStringBuilder("Posted by: " + mTopic.getPostedBy().getUsername());
        int INT_START = 11; int INT_END = INT_START + mTopic.getPostedBy().getUsername().length();
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                INT_START, INT_END, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextAuthor.setText(str);
        mTextTitle.setText(mTopic.getTitle());
        setTitle(mTopic.getTitle());
        mTextMessage.setText(mTopic.getRequest());

        mButtonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // message person
            }
        });

        mButtonSeeOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MapsActivity.class));
            }
        });

        mButtonPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinTopic();
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

    private void pinTopic(){
        Long topicID = mTopic.getID();
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    Pin p = new Pin();
                    p.setTopicID(topicID);
                    bgRealm.insertOrUpdate(p);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Toast.makeText(
                            getBaseContext(),
                            "Succesfully pinned Topic: " + mTopic.getTitle(),
                            Toast.LENGTH_LONG).show();
                }
//            }, new Realm.Transaction.OnError() {
//                @Override
//                public void onError(Throwable error) {
//                    Toast.makeText(
//                            getActivity(),
//                            "Topic " + mTopic.getTitle() + " is already in your pinned list.",
//                            Toast.LENGTH_LONG).show();
//                }
            });
        }
    }

    @Override
    public String getActivityName() {
        return activityName;
    }
}
