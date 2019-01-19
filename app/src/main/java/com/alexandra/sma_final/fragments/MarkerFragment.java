package com.alexandra.sma_final.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.alexandra.sma_final.R;
import com.alexandra.sma_final.activities.MapsActivity;
import com.alexandra.sma_final.customviews.MontserratTextView;

import android.widget.Button;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmQuery;
import realm.Pin;
import realm.Topic;

public class MarkerFragment extends Fragment {

    private Topic mTopic;
//    private MapView mMapView;
    private Button mButtonMessage;
    private Button mButtonPin;
    private MontserratTextView mTextTitle;
    private MontserratTextView mTextRequest;
    private MontserratTextView mTextAuthor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.activity_marker, container, false);
        v.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();

                if (action == MotionEvent.ACTION_DOWN) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }

                return true;
            }
        });

        Long id = getArguments().getLong("topic_id");

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmQuery<Topic> topic  = realm.where(Topic.class).equalTo("id",id);
                if (topic.count() > 0){
                    mTopic = topic.findFirst();

                    if (mTopic == null){

                    }
                }
            });
        }

        mButtonMessage = v.findViewById(R.id.message_author);
        mButtonPin = v.findViewById(R.id.pin);
        mTextAuthor = v.findViewById(R.id.author_marker);
        mTextTitle = v.findViewById(R.id.title_marker);
        mTextRequest = v.findViewById(R.id.request_marker);

        SpannableStringBuilder str = new SpannableStringBuilder("Posted by: " + mTopic.getPostedBy().getUsername());
        int INT_START = 11; int INT_END = INT_START + mTopic.getPostedBy().getUsername().length();
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                INT_START, INT_END, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextAuthor.setText(str);

        mTextAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getContext(), MapsActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mIntent.putExtra("username_id", mTopic.getPostedBy().getId());
                getContext().startActivity(mIntent);
            }
        });

        mTextTitle.setText(mTopic.getTitle());
        mTextRequest.setText(mTopic.getMessage().getText());

        mButtonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // message person
            }
        });

        mButtonPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinTopic();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                            getActivity(),
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
}
