package com.alexandra.sma_final;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alexandra.sma_final.rest.UserDTO;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.Topic;
import realm.User;

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
        activeRequests = getActiveRequests();
        mAdapter = new ActiveRequestsAdapter(activeRequests);
        mActiveRequests.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private ArrayList<Topic> getActiveRequests(){
        ArrayList<Topic> activeR = new ArrayList<>();
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<Topic> topics  = realm.where(Topic.class).findAll();
                for(Topic t : topics){
                    if(((UserActivity)getActivity()).getUser() != null){
                        if(t.getPostedBy().getId() == ((UserActivity)getActivity()).getUser().getId()) { //TODO: change to getID() when using server
                            activeR.add(t);
                        }
                    }else if(((UserActivity)getActivity()).getCurrentUser() != null){
                        if(t.getPostedBy().getId() == ((UserActivity)getActivity()).getCurrentUser().getID()) { //TODO: change to getID() when using server
                            activeR.add(t);
                        }
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
            public Button mResolveButton;

            public MyViewHolder(View itemView){
                super(itemView);

                this.topicTitle = (TextView) itemView.findViewById(R.id.topic_title);
                this.mSeeMoreButton = (Button) itemView.findViewById(R.id.see_more_);
                this.mResolveButton = (Button) itemView.findViewById(R.id.resolve);
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
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mIntent.putExtra("topic_id", topicID);
                    v.getContext().startActivity(mIntent);
                }
            });

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
