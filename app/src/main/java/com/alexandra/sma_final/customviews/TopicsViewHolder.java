package com.alexandra.sma_final.customviews;

import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alexandra.sma_final.R;
import com.alexandra.sma_final.activities.RequestActivity;

import io.realm.Realm;
import io.realm.RealmResults;
import realm.Pin;
import realm.Topic;

public class TopicsViewHolder extends ChildViewHolder {

    private CityCAdapter adapter;
    private MontserratTextView mTitleAuthor;
    private MontserratTextView mRequestLimit;
    private Button mSeeMore;
    private Button mUnpin;
//    private ImageView mImageCard;

//    final Random rnd = new Random();

    public TopicsViewHolder(View itemView, String caller, CityCAdapter adapter) {
        super(itemView);
        this.mTitleAuthor = (MontserratTextView) itemView.findViewById(R.id.title_author);
        this.mRequestLimit = (MontserratTextView) itemView.findViewById(R.id.request_limit);
        this.mSeeMore = (Button) itemView.findViewById(R.id.see_more);
        this.mUnpin = (Button) itemView.findViewById(R.id.unpin);

//        this.mImageCard = (ImageView) itemView.findViewById(R.id.card_view);

//        final String img = "random_" + rnd.nextInt(6);
//        switch (img){
//            case "random_1":
//                this.mImageCard.setImageResource(R.color.random_1);
//                break;
//            case "random_2":
//                this.mImageCard.setImageResource(R.color.random_2);
//                break;
//            case "random_3":
//                this.mImageCard.setImageResource(R.color.random_3);
//                break;
//            case "random_4":
//                this.mImageCard.setImageResource(R.color.random_4);
//                break;
//            case "random_5":
//                this.mImageCard.setImageResource(R.color.random_5);
//                break;
//            default: this.mImageCard.setImageResource(R.color.backgroundLight); break;
//        }

        this.adapter = adapter;

        if (caller == "MYPINS"){
            this.mUnpin.setVisibility(View.VISIBLE);
        }else{
            this.mUnpin.setVisibility(View.GONE);
        }

        this.mUnpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unpinTopic(itemView);
            }
        });

        this.mSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeMoreButtonClicked(itemView);
            }
        });
    }

    private void unpinTopic(View view){
        Long topicID = ((Topic) adapter.mItemList.get(getAdapterPosition())).getId();
        Topic t = (Topic) adapter.mItemList.get(getAdapterPosition());

        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
//                        Long topicID = mTopics.get(myViewHolder.getAdapterPosition()).getId();
                    RealmResults<Pin> rows = bgRealm.where(Pin.class).equalTo("topicID", topicID).findAll();
                    rows.deleteAllFromRealm();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Toast.makeText(
                            view.getContext(),
                            "Succesfully deleted pin",
                            Toast.LENGTH_LONG).show();
                    int idx_parent = 0;
                    for(int i = 0; i < adapter.mParentItemList.size(); i++){
                        if( ((CityC) adapter.mParentItemList.get(i)).getName().equals(t.getCity()) ){
                            idx_parent = i;
                            for(int j = 0; j < ((CityC) adapter.mParentItemList.get(i)).getChildItemList().size(); j++){
                                if( ((Topic)((CityC) adapter.mParentItemList.get(i)).getChildItemList().get(j)).getId() == topicID){
                                    ((CityC) adapter.mParentItemList.get(i)).getChildItemList().remove(j);
                                    break;
                                }
                            }
                        }
                    }
                    int idx_child = 0;
                    for(int i = 0; i < adapter.mItemList.size(); i++){
                        if(adapter.mItemList.get(i) instanceof Topic){
                            if( ((Topic)adapter.mItemList.get(i)).getId() == topicID ){
                                idx_child = i;
                            }
                        }
                    }

                    adapter.mItemList.remove(idx_child);
                    adapter.notifyItemRemoved(idx_child);
                }
            });
        }
    }

    private void seeMoreButtonClicked(View view){
        Long topicID = ((Topic) adapter.mItemList.get(getAdapterPosition())).getId();

        Intent mIntent = new Intent(view.getContext(), RequestActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mIntent.putExtra("topic_id", topicID);
        view.getContext().startActivity(mIntent);
    }

    public void bind(Topic topic) {

        SpannableStringBuilder str1 = new SpannableStringBuilder(topic.getTitle() + " - ");
        int INT_START1 = 0; int INT_END1 = topic.getTitle().length();
        str1.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                INT_START1, INT_END1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder str2 = new SpannableStringBuilder("Posted by: " +
                topic.getPostedBy().getUsername());
        int INT_START2 = 11; int INT_END2 = INT_START2 + topic.getPostedBy().getUsername().length();
        str2.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                INT_START2, INT_END2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str1.append(str2);
        mTitleAuthor.setText(str1);

        mRequestLimit.setText(topic.getMessage().getText().trim());
    }
}
