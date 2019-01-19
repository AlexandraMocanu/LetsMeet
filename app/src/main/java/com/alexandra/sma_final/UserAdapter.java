package com.alexandra.sma_final;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.alexandra.sma_final.view.MontserratTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.City;
import realm.User;

class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private ArrayList<User> mUsers;
    private Context context;

    final Random rnd = new Random();

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public MontserratTextView username;
        public MontserratTextView title;
        public MontserratTextView time;
        public ImageView mImage;
        public CardView mCardView;

        public MyViewHolder(View itemView){
            super(itemView);

            this.username = (MontserratTextView) itemView.findViewById(R.id.username);
            this.title = (MontserratTextView) itemView.findViewById(R.id.title);
            this.time = (MontserratTextView) itemView.findViewById(R.id.time);
            this.mImage = (ImageView) itemView.findViewById(R.id.icon);
            this.mCardView = (CardView) itemView.findViewById(R.id.card_view);

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

        MontserratTextView MontserratTextView = viewHolder.username;
        MontserratTextView.setText(user.getUsername());

        MontserratTextView title = viewHolder.username;
        title.setText("last message"); //TODO: change this to actual last mesage in chat

        MontserratTextView time =  viewHolder.time;
        time.setText("00:00");

        ImageView mImage = viewHolder.mImage;
        final String img = "user_" + rnd.nextInt(10);
        mImage.setImageDrawable(
                context.getResources().getDrawable(((MainActivity)context).getResourceID(img, "drawable",
                        context))
        );

        CardView cardView = viewHolder.mCardView;
        cardView.setOnClickListener(new View.OnClickListener() {
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
