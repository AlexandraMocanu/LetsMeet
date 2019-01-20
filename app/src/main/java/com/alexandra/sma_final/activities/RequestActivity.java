package com.alexandra.sma_final.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.customviews.MontserratTextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import realm.Conversation;
import realm.Pin;
import realm.Topic;

public class RequestActivity extends BaseActivity {

    protected String activityName = "Request Details";

    private Button mButtonMessage;
    private Button mButtonSeeOnMap;
    private Button mButtonPin;
    private boolean pinOrUnpin;
    private ImageView mRandomPicture;
    private MontserratTextView mTextTitle;
    private MontserratTextView mTextMessage;
    private MontserratTextView mTextAuthor;
    private MontserratTextView mTextPostedBy;
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
        mButtonMessage.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Montserrat-Regular.ttf"));
        this.mButtonSeeOnMap = (Button) findViewById(R.id.see_map);
        mButtonSeeOnMap.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Montserrat-Regular.ttf"));
        this.mButtonPin = (Button) findViewById(R.id.pin_button);
        mButtonPin.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Montserrat-Regular.ttf"));
        this.mRandomPicture = (ImageView) findViewById(R.id.random_picture);
        this.mTextTitle = (MontserratTextView) findViewById(R.id.title);
        this.mTextMessage = (MontserratTextView) findViewById(R.id.message);
        this.mTextAuthor = (MontserratTextView) findViewById(R.id.author);
        this.mTextPostedBy = (MontserratTextView) findViewById(R.id.postedbytext);

        topicID = getIntent().getExtras().getLong("topic_id");

        final String img = "img_" + rnd.nextInt(11);
        this.mRandomPicture.setImageDrawable(
                getResources().getDrawable(getResourceID(img, "drawable",
                        getApplicationContext()))
                );

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmQuery<Topic> topic  = realm.where(Topic.class).equalTo("id",topicID);
                if (topic.count() > 0){
                    setTopic(topic.findFirst());
                }
            });
        }

        mTextAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), UserActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String username = mTopic.getPostedBy().getUsername();
                mIntent.putExtra("username", username);
                getApplicationContext().startActivity(mIntent);
            }
        });

        SpannableStringBuilder str = new SpannableStringBuilder("anon/" + mTopic.getPostedBy().getUsername() + " ");
        int INT_START = 0; int INT_END = mTopic.getPostedBy().getUsername().length();
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                INT_START, INT_END, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextAuthor.setText(str);
        mTextTitle.setText(mTopic.getTitle());
        setTitle(mTopic.getTitle());
        mTextMessage.setText(mTopic.getMessage().getText());

        mButtonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), ChatActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String username = mTopic.getPostedBy().getUsername();
                Long id = mTopic.getId();
                Conversation conv  = new Conversation();
                conv.setRespondingUserId(((MyApplication)getApplicationContext()).getCurrentUser().getId());
                conv.setTopicId(id);
                ((MyApplication)getApplicationContext()).requestGateway.putConversation(conv);
                mIntent.putExtra("usernameTopic", username); //username author
                mIntent.putExtra("usernameRespond", ((MyApplication)getApplicationContext()).getCurrentUser().getId()); //responding user id = me
                mIntent.putExtra("idTopic", id); //topic id
                getApplicationContext().startActivity(mIntent);
            }
        });

        mButtonSeeOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(v.getContext(), MapsActivity.class);
                mIntent.putExtra("topicId", topicID);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<Pin> pins  = realm.where(Pin.class).findAll();
                if (pins.size() != 0){
                    for (Pin p: pins){
                        if(p.getTopicID().equals(topicID)){
                            pinOrUnpin = true;
                        }
                    }
                }
            });
        }

        if(pinOrUnpin == true){
            mButtonPin.setText("Unpin");
        }else{
            mButtonPin.setText("Pin");
        }

        mButtonPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pinOrUnpin == true){
                    unpinTopic();
                    mButtonPin.setText("Pin");
                    pinOrUnpin = false;
                }else{
                    pinTopic();
                    mButtonPin.setText("Unpin");
                    pinOrUnpin = true;
                }
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

    @Override
    public String getActivityName() {
        return activityName;
    }

    private void pinTopic(){
        Long topicID = mTopic.getId();
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
            });
        }
    }

    private void unpinTopic(){
        Long topicID = mTopic.getId();
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    RealmResults<Pin> rows = bgRealm.where(Pin.class).equalTo("topicID", topicID).findAll();
                    rows.deleteAllFromRealm();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Toast.makeText(
                            getBaseContext(),
                            "Succesfully deleted Pin: " + mTopic.getTitle(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setTopic(Topic t){
        this.mTopic = t;
    }
}
