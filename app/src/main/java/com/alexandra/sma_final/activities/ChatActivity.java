package com.alexandra.sma_final.activities;

import android.opengl.Visibility;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.adapters.MessageListAdapter;
import com.alexandra.sma_final.customviews.MontserratEditText;
import com.alexandra.sma_final.rest.UserDTO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.Conversation;
import realm.Message;
import realm.User;

public class ChatActivity extends BaseActivity {

    protected String activityName = "Messenger";

    private static RecyclerView mMessageRecycler;
    private static MessageListAdapter mMessageAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Message> messages;
    private UserDTO mCurrentUser;
    private User mRespondingUser;
    private static Conversation conversation;

    private Button sendButton;
    private MontserratEditText writeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        sendButton = (Button) findViewById(R.id.button_chatbox_send);
        writeMessage = (MontserratEditText) findViewById(R.id.edittext_chatbox);

        setListeners();
        mActivity.setText(getActivityName());

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        messages = new ArrayList<Message>();

        //set responding user = author of topic
        String respondingUser = getIntent().getExtras().getString("usernameTopic");
        ((MyApplication)getApplication()).requestGateway.getUserByUsername(respondingUser);
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final User user  = realm.where(User.class).findFirst();
                if (user != null){
                    setUser(user);
                }
            });
        }

        //set current user
        Long currentUserId = getIntent().getExtras().getLong("usernameRespond_id");
        if(currentUserId == ((MyApplication)getApplication()).getCurrentUser().getId()){
            mCurrentUser = ((MyApplication)getApplication()).getCurrentUser();
        }

        //set conversation
        Long topicId = getIntent().getExtras().getLong("idTopic");
        //get conversation from Realm
        ((MyApplication)getApplication()).requestGateway.getUserConversations();
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<Conversation> convs  = realm.where(Conversation.class).equalTo("topicId", topicId).findAll();
                for(Conversation c : convs){
                    if(c.getRespondingUserId() == mCurrentUser.getId()){
                        if(c != null){
                            setConversation(c);
                        }
                    }
                }
            });
        }

        //set messages
        messages = new ArrayList<>();
        messages.addAll(conversation.getMessages());
        if(messages.size() > 2){
            messages.sort(new Comparator<Message>() {
                @Override
                public int compare(Message o1, Message o2) {
                    if(o1.getTimestampMillis() > o2.getTimestampMillis()){
                        return 1;
                    }else if(o1.getTimestampMillis() < o2.getTimestampMillis()){
                        return -1;
                    }
                    return 0;
                }
            });
        }

        mMessageAdapter = new MessageListAdapter(this, messages);
        mLayoutManager = new LinearLayoutManager(this);
        mMessageRecycler.setLayoutManager(mLayoutManager);
        mMessageRecycler.setAdapter(mMessageAdapter);

        //TODO: send button
    }

    private void setUser(User u){
        mRespondingUser = u;
    }

    private void setConversation(Conversation c){
        conversation = c;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mLayoutManager.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLayoutManager.onRestoreInstanceState(savedInstanceState);
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
