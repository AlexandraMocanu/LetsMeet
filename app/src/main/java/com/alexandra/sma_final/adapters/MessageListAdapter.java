package com.alexandra.sma_final.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alexandra.sma_final.activities.ChatActivity;
import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.customviews.MontserratTextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.Message;
import realm.User;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> mMessageList;

    public MessageListAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) mMessageList.get(position);

        if (message.getUserId().equals(((MyApplication)((ChatActivity)mContext).getApplication()).getCurrentUser().getId())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view, mContext);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        MontserratTextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (MontserratTextView) itemView.findViewById(R.id.text_message_body);
            timeText = (MontserratTextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            Date date = new Date(message.getTimestampMillis());
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            //TODO: timezone?
//        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateFormatted = formatter.format(date);
            timeText.setText(dateFormatted);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        MontserratTextView messageText, timeText, nameText;
        ImageView profileImage;
        Context mContext;

        final Random rnd = new Random();

        ReceivedMessageHolder(View itemView, Context context) {
            super(itemView);
            mContext = context;
            messageText = (MontserratTextView) itemView.findViewById(R.id.text_message_body);
            timeText = (MontserratTextView) itemView.findViewById(R.id.text_message_time);
            nameText = (MontserratTextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Message message) {
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            Date date = new Date(message.getTimestampMillis());
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            //TODO: timezone?
//        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateFormatted = formatter.format(date);
            timeText.setText(dateFormatted);
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        RealmResults<User> users = bgRealm.where(User.class).equalTo("id", message.getUserId()).findAll();
                        nameText.setText(users.first().getUsername());
                    }
                });
            }

            final String img = "user_" + rnd.nextInt(10);
            this.profileImage.setImageDrawable(
                    mContext.getResources().getDrawable(((ChatActivity)mContext).getResourceID(img, "drawable",
                            mContext)));

        }
    }
}


