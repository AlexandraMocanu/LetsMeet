package com.alexandra.sma_final.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.activities.ChatActivity;
import com.alexandra.sma_final.activities.MainActivity;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.customviews.MontserratTextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.City;
import realm.Conversation;
import realm.Message;
import realm.User;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    private ArrayList<Conversation> mConversations;
    private User currentUserConv;
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

    public ConversationAdapter(ArrayList<Conversation> results, Context context){
        mConversations = results;
        this.context = context;
        currentUserConv = null;
    }

    @Override
    public ConversationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View userView = inflater.inflate(R.layout.cardview_chat, parent, false);

        ConversationAdapter.MyViewHolder vh = new ConversationAdapter.MyViewHolder(userView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ConversationAdapter.MyViewHolder viewHolder, int position) {
        Conversation conv = mConversations.get(position);

        //find responding user
//        ((MyApplication)((MainActivity)context).getApplication()).requestGateway.getAllUsers();
        MontserratTextView username = viewHolder.username;
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                if(conv.getRespondingUserId() != null){
                    final RealmResults<User> user  = realm.where(User.class).equalTo("id", conv.getRespondingUserId()).findAll();
                    if (user.size() != 0){
                        User u = user.first();
                        setUserConv(u);
                    }
                }
            });
        }

        //find messages
        MontserratTextView title = viewHolder.title;
        MontserratTextView time =  viewHolder.time;
        ArrayList<Message> messages = new ArrayList<>();
        messages.addAll(conv.getMessages());

        if(!messages.isEmpty() && currentUserConv != null){
            username.setText(currentUserConv.getUsername());

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
            title.setText(messages.get(messages.size()-1).getText());

            //set timestamp
            Date date = new Date(messages.get(messages.size()-1).getTimestampMillis());
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateFormatted = formatter.format(date);
            time.setText(dateFormatted);

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

                    String username = ((MyApplication)((MainActivity)context).getApplication()).getCurrentUser().getUsername();
                    if(!username.equals("fallback-user-1")){
                        Intent mIntent = new Intent(context, ChatActivity.class);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mIntent.putExtra("usernameTopic", username); //username author
                        mIntent.putExtra("usernameRespond", currentUserConv.getId()); //responding user id
                        mIntent.putExtra("idTopic", conv.getTopicId()); //topic id
                        context.startActivity(mIntent);
                    }
                }
            });
        }
    }

    private void setUserConv(User u){
        currentUserConv = u;
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public void addConv(Conversation u){
        mConversations.add(u);
    }

    public boolean contains(Conversation c){
        for(int i = 0; i < mConversations.size(); i++){
            if(mConversations.get(i).getId().equals(c.getId())){
                return true;
            }
        }
        return false;
    }
}
