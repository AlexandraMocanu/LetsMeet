package com.alexandra.sma_final.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.activities.RequestActivity;
import com.alexandra.sma_final.activities.UserActivity;
import com.alexandra.sma_final.customviews.MontserratTextView;
import com.alexandra.sma_final.rest.UserDTO;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.Topic;
import realm.User;

public class ActiveRequests extends Fragment {

    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Topic> mActiveRequestsList;
    private RecyclerView mActiveRequestsView;
    private ActiveRequestsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.reqs_tab, container, false);

        //set recyvler view
        mActiveRequestsView = (RecyclerView) v.findViewById(R.id.active_requests);
        mLayoutManager = new LinearLayoutManager(getContext());
        mActiveRequestsView.setLayoutManager(mLayoutManager);
        getActiveRequests();
        mAdapter = new ActiveRequestsAdapter(mActiveRequestsList);
        mActiveRequestsView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getActiveRequests(){
        mActiveRequestsList = new ArrayList<Topic>();

        //TODO: ((MyApplication)getContext()).requestGateway.getUserTopics();
        //TODO: get topics by user!! not all topics
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                User userProfile = ((UserActivity)getActivity()).getUserProfileOwner();
                final RealmResults<Topic> allTopics;
                if (userProfile != null){ //if we are on someone elses profile
//                    final RealmResults<Topic> topicsByUser = realm.where(Topic.class).equalTo(postedBy, userProfile).findAll();
                    allTopics = realm.where(Topic.class).findAll();
                    for(Topic t : allTopics) {
                        if (t.getPostedBy().getUsername().equals(userProfile.getUsername())) {
                            addTopic(t);
                        }
                    }
                }
                else if (((UserActivity)getContext()).getmCurrentUser() != null){ // if its our profile
//                    final RealmResults<Topic> topicsByUser = realm.where(Topic.class).equalTo(postedBy, userProfile).findAll();
                    allTopics = realm.where(Topic.class).findAll();
                    for(Topic t : allTopics) {
                        if (t.getPostedBy().getUsername().equals(((UserActivity)getContext()).getmCurrentUser().getUsername())) {
                            addTopic(t);
                        }
                    }
                }
            });
        }
    }

    private void addTopic(Topic t){
        mActiveRequestsList.add(t);
    }

    private class ActiveRequestsAdapter extends RecyclerView.Adapter<ActiveRequestsAdapter.MyViewHolder> {

        private ArrayList<Topic> mActiveRequests;

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public MontserratTextView topicTitle;
            public Button mSeeMoreButton;
            public Button mResolveButton;
            ConstraintLayout constraintLayout;

            public MyViewHolder(View itemView){
                super(itemView);

                this.mResolveButton = (Button) itemView.findViewById(R.id.resolve);
                mResolveButton.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/Montserrat-Regular.ttf"));
                if(((UserActivity)getActivity()).getUserProfileOwner() != null){ // not our profile so we can't rezolve topics
                    mResolveButton.setActivated(false);
                    mResolveButton.setVisibility(View.INVISIBLE);
                }
                this.topicTitle = (MontserratTextView) itemView.findViewById(R.id.topic_title);
                this.mSeeMoreButton = (Button) itemView.findViewById(R.id.see_more_);
                mSeeMoreButton.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/Montserrat-Regular.ttf"));

                constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraintLayout);

            }

        }

        public ActiveRequestsAdapter(ArrayList<Topic> results){
            mActiveRequests = results;
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
            Topic topic = mActiveRequests.get(position);

            MontserratTextView MontserratTextView = viewHolder.topicTitle;
            MontserratTextView.setText(topic.getTitle().substring(0, (topic.getTitle().length() > 35 ? 30 : topic.getTitle().length())) + " ...");
            Button seeMoreButton = viewHolder.mSeeMoreButton;
            seeMoreButton.setText("See More");
            seeMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long topicID = topic.getId();

                    Intent mIntent = new Intent(v.getContext(), RequestActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mIntent.putExtra("topic_id", topicID);
                    v.getContext().startActivity(mIntent);
                }
            });

            if(((UserActivity)getActivity()).getUserProfileOwner() != null) {
                Button resolveButton = viewHolder.mResolveButton;
                resolveButton.setText("Resolve");
                resolveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Long topicID = topic.getId();

                        //TODO: put archived topic
                    }
                });

//                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)seeMoreButton.getLayoutParams();
////                params.endToEnd = 1;
//                params.startToEnd = 1;
//                seeMoreButton.setLayoutParams(params);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(viewHolder.constraintLayout);

                constraintSet.connect(seeMoreButton.getId(), constraintSet.END,
                        viewHolder.constraintLayout.getId(), constraintSet.END, 8);

                constraintSet.applyTo(viewHolder.constraintLayout);

            }
        }

        @Override
        public int getItemCount() {
            return mActiveRequests.size();
        }

        public boolean contains(Topic c){
            for(int i = 0; i < mActiveRequests.size(); i++){
                if(mActiveRequests.get(i).getId() == c.getId()){
                    return true;
                }
            }
            return false;
        }
    }

}
