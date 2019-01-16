package com.alexandra.sma_final;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.City;
import realm.User;

class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private ArrayList<User> mUsers;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView username;

        public MyViewHolder(View itemView){
            super(itemView);

            this.username = (TextView) itemView.findViewById(R.id.username);
        }
    }

    public UserAdapter(ArrayList<User> results, Context context){
        mUsers = results;
        this.context = context;
    }

    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View userView = inflater.inflate(R.layout.cardview_chat, parent, false);

        UserAdapter.MyViewHolder vh = new UserAdapter.MyViewHolder(userView);
        return vh;
    }

    @Override
    public void onBindViewHolder(UserAdapter.MyViewHolder viewHolder, int position) {
        User user = mUsers.get(position);

        TextView textView = viewHolder.username;
        textView.setText(user.getUsername());

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context, ChatActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mIntent.putExtra("username", user.getUsername());
                context.startActivity(mIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void addCity(User u){
        mUsers.add(u);
    }

    public boolean contains(City c){
        for(int i = 0; i < mUsers.size(); i++){
            if(mUsers.get(i).getUsername().equals(c.getName())){
                return true;
            }
        }
        return false;
    }
}
