//package com.alexandra.sma_final;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.constraint.ConstraintLayout;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.RecyclerView;
//import android.text.Spannable;
//import android.text.SpannableStringBuilder;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import com.alexandra.sma_final.view.Button;
//import android.widget.ImageView;
//import com.alexandra.sma_final.view.MontserratTextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//
//import io.realm.Realm;
//import io.realm.RealmResults;
//import realm.Pin;
//import realm.Topic;
//
//public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {
//
//        private String caller;
//        private ArrayList<Topic> mTopics;
//
//        public static class MyViewHolder extends RecyclerView.ViewHolder {
//
//            private ConstraintLayout mConstraintLayout;
//            private MontserratTextView mTitleAuthor;
//            private MontserratTextView mRequestLimit;
//            private Button mSeeMore;
//            private Button mUnpin;
//
//            public MyViewHolder(View itemView, String caller) {
//                super(itemView);
//                this.mTitleAuthor = (MontserratTextView) itemView.findViewById(R.id.title_author);
//                this.mRequestLimit = (MontserratTextView) itemView.findViewById(R.id.request_limit);
//                this.mSeeMore = (Button) itemView.findViewById(R.id.see_more);
//                this.mUnpin = (Button) itemView.findViewById(R.id.unpin);
//
//                if (caller == "MYPINS"){
//                    this.mUnpin.setVisibility(View.VISIBLE);
//                }else{
//                    this.mUnpin.setVisibility(View.GONE);
//                }
//            }
//        }
//
//        public RequestAdapter(ArrayList<Topic> data, String caller) {
//            this.mTopics = data;
//            this.caller = caller;
//        }
//
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup parent,
//                                               int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.cardview_dashboard, parent, false);
//
//            MyViewHolder myViewHolder = new MyViewHolder(view, caller);
//
//            myViewHolder.mUnpin.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    unpinTopic(myViewHolder, view);
//                }
//            });
//
//            myViewHolder.mSeeMore.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    seeMoreButtonClicked(myViewHolder, view);
//                }
//            });
//
//            return myViewHolder;
//        }
//
//        private void unpinTopic(MyViewHolder myViewHolder, View view){
//            Long topicID = mTopics.get(myViewHolder.getAdapterPosition()).getId();
//
//            try (Realm realm = Realm.getDefaultInstance()) {
//                realm.executeTransactionAsync(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm bgRealm) {
////                        Long topicID = mTopics.get(myViewHolder.getAdapterPosition()).getId();
//                        RealmResults<Pin> rows = bgRealm.where(Pin.class).equalTo("topicID", topicID).findAll();
//                        rows.deleteAllFromRealm();
//                    }
//                }, new Realm.Transaction.OnSuccess() {
//                    @Override
//                    public void onSuccess() {
//                        Toast.makeText(
//                                view.getContext(),
//                                "Succesfully deleted pin",
//                                Toast.LENGTH_LONG).show();
//                        mTopics.remove(mTopics.get(myViewHolder.getAdapterPosition()));
//                        notifyItemRemoved(myViewHolder.getAdapterPosition());
//                    }
////                }, new Realm.Transaction.OnError() {
////                    @Override
////                    public void onError(Throwable error) {
////                        Toast.makeText(
////                                view.getContext(),
////                                "Topic doesn't exist in your pins.",
////                                Toast.LENGTH_LONG).show();
////                    }
//                });
//            }
//        }
//
//        private void seeMoreButtonClicked(MyViewHolder myViewHolder, View view){
//            Long topicID = mTopics.get(myViewHolder.getAdapterPosition()).getId();
//
//            Intent mIntent = new Intent(view.getContext(), RequestActivity.class);
//            mIntent.putExtra("topic_id", topicID);
//            view.getContext().startActivity(mIntent);
//        }
//
//        @Override
//        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
//
//            MontserratTextView titleAuthor = holder.mTitleAuthor;
//            MontserratTextView request = holder.mRequestLimit;
//
//            SpannableStringBuilder str1 = new SpannableStringBuilder(mTopics.get(listPosition).getTitle());
//            int INT_START1 = 0; int INT_END1 = mTopics.get(listPosition).getTitle().length();
//            str1.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
//                    INT_START1, INT_END1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            SpannableStringBuilder str2 = new SpannableStringBuilder(str1 + " Posted by: " +
//                    mTopics.get(listPosition).getPostedBy().getUsername());
//            int INT_START2 = INT_END1 + 12; int INT_END2 = INT_START2 + mTopics.get(listPosition).getPostedBy().getUsername().length();
//            str2.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
//                    INT_START2, INT_END2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            titleAuthor.setText(str2);
//
//            request.setText(mTopics.get(listPosition).getRequest());
//        }
//
//        @Override
//        public int getItemCount() {
//            return mTopics.size();
//        }
//
//    }