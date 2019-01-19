package com.alexandra.sma_final.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alexandra.sma_final.R;
import com.alexandra.sma_final.activities.RequestActivity;
import com.alexandra.sma_final.activities.UserActivity;
import com.alexandra.sma_final.customviews.MontserratTextView;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.Topic;

public class ActiveRequests extends Fragment {

    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Topic> activeRequests;
    private RecyclerView mActiveRequests;
    private ActiveRequestsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.reqs_tab, container, false);

        //set recyvler view
        mActiveRequests = (RecyclerView) v.findViewById(R.id.active_requests);
        mLayoutManager = new LinearLayoutManager(getContext());
        mActiveRequests.setLayoutManager(mLayoutManager);
        getActiveRequests();
        mAdapter = new ActiveRequestsAdapter(activeRequests);
        mActiveRequests.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getActiveRequests(){
        activeRequests = new ArrayList<Topic>();
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<Topic> topics  = realm.where(Topic.class).findAll();
                for(Topic t : topics){
                    if(((UserActivity)getActivity()).getCurrentUser() != null){
                        if(t.getPostedBy().getId() == ((UserActivity)getActivity()).getCurrentUser().getId()) { //TODO: change to getID() when using server
                            addTopic(t);
                        }
                    }
                }
            });
        }
    }

    private void addTopic(Topic t){
        activeRequests.add(t);
    }

    private class ActiveRequestsAdapter extends RecyclerView.Adapter<ActiveRequestsAdapter.MyViewHolder> {

        private ArrayList<Topic> activeRequests;

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public MontserratTextView topicTitle;
            public Button mSeeMoreButton;
            public Button mResolveButton;

            public MyViewHolder(View itemView){
                super(itemView);

                this.mResolveButton = (Button) itemView.findViewById(R.id.resolve);
                mResolveButton.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/Montserrat-Regular.ttf"));
                if(((UserActivity)getActivity()).getCurrentUser() != null){
                    mResolveButton.setActivated(false);
                    mResolveButton.setVisibility(View.INVISIBLE);
                }
                this.topicTitle = (MontserratTextView) itemView.findViewById(R.id.topic_title);
                this.mSeeMoreButton = (Button) itemView.findViewById(R.id.see_more_);
                mSeeMoreButton.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/Montserrat-Regular.ttf"));

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

            MontserratTextView MontserratTextView = viewHolder.topicTitle;
            MontserratTextView.setText(topic.getTitle().substring(0, (topic.getTitle().length() > 15 ? 15 : topic.getTitle().length() - 2)) + "...");
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

            if(((UserActivity)getActivity()).getCurrentUser() != null) {
                Button resolveButton = viewHolder.mResolveButton;
                resolveButton.setText("Resolve");
                resolveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Long topicID = topic.getId();

                        //TODO: put archived topic
                    }
                });
            }
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
